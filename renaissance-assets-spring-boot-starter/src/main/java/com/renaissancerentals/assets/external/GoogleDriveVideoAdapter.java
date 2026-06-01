package com.renaissancerentals.assets.external;

import static com.google.api.client.json.gson.GsonFactory.getDefaultInstance;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.renaissancerentals.assets.error.AssetsBusinessException;
import com.renaissancerentals.assets.error.AssetsErrorCode;
import com.renaissancerentals.assets.error.AssetsUploadException;
import com.renaissancerentals.assets.model.PagedResult;
import com.renaissancerentals.assets.model.video.InitiateUploadRequest;
import com.renaissancerentals.assets.model.video.VideoAsset;
import com.renaissancerentals.assets.model.video.VideoUploadMetadata;
import com.renaissancerentals.assets.service.VideoService;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GoogleDriveVideoAdapter implements VideoService {

    private static final String UPLOAD_URL_FORMAT = "https://www.googleapis.com/upload/drive/v3/files%s";

    private static final String VIDEO_FIELDS =
            "id, name, description, mimeType, parents, thumbnailLink, webContentLink";

    private final Drive drive;
    private final HttpRequestFactory requestFactory;
    private final Credential credential;

    public GoogleDriveVideoAdapter(final GoogleDriveFactory driveFactory) {
        this.requestFactory = driveFactory.createHttpRequestFactory();
        this.drive = driveFactory.createDrive();
        this.credential = driveFactory.getCredential();
    }

    @Override
    public Optional<VideoAsset> get(final String id) {
        try {
            File file = drive.files().get(id).setFields(VIDEO_FIELDS).execute();
            log.debug("Getting drive asset for assetId: {}", id);
            return Optional.of(buildVideoMetadata(file));
        } catch (IOException | RuntimeException e) {
            log.error("Error getting video for id {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<VideoAsset> getBy(String folderId, String name) {
        try {
            log.debug("Getting by folderId: {} for name: {} ", folderId, name);
            Drive.Files.List list = drive.files()
                    .list()
                    .setQ(String.format("'%s' in parents and name = '%s'", folderId, name))
                    .setPageSize(PAGE_SIZE)
                    .setOrderBy("modifiedTime desc")
                    .setFields("nextPageToken, files(" + VIDEO_FIELDS + ")");

            FileList result = list.execute();

            return result.getFiles().stream()
                    .filter(file -> file.getMimeType().contains("video"))
                    .map(this::buildVideoMetadata)
                    .findFirst();

        } catch (IOException e) {
            throw new AssetsBusinessException(AssetsErrorCode.ASSET_INPUT_OUTPUT_ERROR, e);
        }
    }

    @Override
    public PagedResult<VideoAsset> listByFolder(
            final String folderId, final Integer pageSize, final String nextPageToken) {
        try {
            log.debug("Listing drive: {} with pageSize: {} and pageToken {}", folderId, pageSize, nextPageToken);
            Drive.Files.List list = drive.files()
                    .list()
                    .setQ("'" + folderId + "' in parents")
                    .setPageSize(Optional.ofNullable(pageSize).orElse(PAGE_SIZE))
                    .setOrderBy("modifiedTime desc")
                    .setFields("nextPageToken, files(" + VIDEO_FIELDS + ")");

            if (nextPageToken != null) {
                list.setPageToken(nextPageToken);
            }

            FileList result = list.execute();
            final var items = result.getFiles().stream()
                    .filter(file -> file.getMimeType().contains("video"))
                    .map(this::buildVideoMetadata)
                    .toList();

            return PagedResult.<VideoAsset>builder()
                    .pageSize(Optional.ofNullable(pageSize).orElse(PAGE_SIZE))
                    .items(items)
                    .nextPageToken(result.getNextPageToken())
                    .build();

        } catch (IOException e) {
            throw new AssetsBusinessException(AssetsErrorCode.ASSET_INPUT_OUTPUT_ERROR, e);
        }
    }

    @Override
    public void delete(String id) {
        try {
            drive.files().delete(id).execute();
        } catch (IOException e) {
            throw new AssetsBusinessException(AssetsErrorCode.ASSET_INPUT_OUTPUT_ERROR, e);
        }
    }

    @Override
    public VideoUploadMetadata initiateUpload(final InitiateUploadRequest uploadRequest) {
        try {

            File fileMetadata = new File();
            fileMetadata.setName(uploadRequest.name());
            fileMetadata.setParents(List.of(uploadRequest.folderId()));
            fileMetadata.setMimeType(uploadRequest.mimeType());
            fileMetadata.setDescription(uploadRequest.description());

            final var video = getBy(uploadRequest.folderId(), uploadRequest.name());
            video.ifPresent(videoAsset -> delete(videoAsset.id()));

            final var request = requestFactory.buildPostRequest(
                    new GenericUrl(String.format(UPLOAD_URL_FORMAT, "?uploadType=resumable")),
                    new JsonHttpContent(getDefaultInstance(), fileMetadata));

            request.getHeaders().setAuthorization("Bearer " + getAccessToken());
            request.getHeaders().set("X-Upload-Content-Type", uploadRequest.mimeType());
            request.getHeaders().set("X-Upload-Content-Length", "*");

            HttpResponse response = request.execute();
            String resumableUploadUrl = response.getHeaders().getLocation();
            log.info("Resumable upload session initiated: {}", resumableUploadUrl);
            var uploadId = extractUploadId(resumableUploadUrl);

            return uploadId.map(id -> new VideoUploadMetadata(uploadId.get(), resumableUploadUrl))
                    .orElseThrow(() -> new AssetsUploadException("Failure to initiate upload: No upload Id present"));
        } catch (IOException | RuntimeException e) {
            throw new AssetsUploadException("Error initiating video upload");
        }
    }

    private VideoAsset buildVideoMetadata(File file) {
        return VideoAsset.builder()
                .id(file.getId())
                .name(file.getName())
                .description(file.getDescription())
                .mimeType(file.getMimeType())
                .folderId(hasParents(file) ? file.getParents().getFirst() : null)
                .videoUrl(file.getWebContentLink())
                .thumbnailUrl(file.getThumbnailLink())
                .build();
    }

    private boolean hasParents(File file) {
        return file.getParents() != null && !file.getParents().isEmpty();
    }

    private Optional<String> extractUploadId(String resumableUploadUrl) {
        Pattern pattern = Pattern.compile("upload_id=([^&]+)");
        Matcher matcher = pattern.matcher(resumableUploadUrl);
        if (matcher.find()) {
            return Optional.of(matcher.group(1)); // Returns the value of upload_id
        }
        return Optional.empty(); // Return null if n
    }

    private String getAccessToken() throws IOException {
        if (credential.refreshToken()) { // Ensures token is always fresh
            return credential.getAccessToken();
        } else {
            throw new AssetsUploadException("Failed to refresh access token");
        }
    }
}
