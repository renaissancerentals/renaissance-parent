package com.renaissancerentals.assets.external;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.renaissancerentals.assets.error.AssetsBusinessException;
import com.renaissancerentals.assets.error.AssetsErrorCode;
import com.renaissancerentals.assets.model.*;
import com.renaissancerentals.assets.service.AssetService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GoogleDriveAssetAdapter implements AssetService {
    private static final String IMAGE_FIELDS = "id, name, description, mimeType, parents, imageMediaMetadata, thumbnailLink, webContentLink";
    public static final String IMAGE = "image";
    private static final String FOLDER_MIME_TYPE = "application/vnd.google-apps.folder";
    private final Drive drive;

    public GoogleDriveAssetAdapter(final GoogleDriveFactory driveFactory) {
        this.drive = driveFactory.createDrive();
    }

    @Override
    public Optional<Asset> get(final String id){
        return execute(() -> {
            var file = drive.files().get(id).setFields(IMAGE_FIELDS).execute();
            return Optional.of(buildAssetFrom(file));
        });
    }

    @Override
    public Optional<byte[]> getFile(final String id){
        return execute(() -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Drive.Files.Get file = drive.files().get(id);
            file.executeMediaAndDownloadTo(outputStream);
            return Optional.of(outputStream.toByteArray());
        });
    }

    @Override
    public Optional<Asset> getBy(final String folderId,final String name){
        return execute(() -> {
            log.debug("Getting by folderId: {} for name: {} ",folderId,name);
            var fileList = drive.files().list().setQ(String.format("'%s' in parents and name = '%s'",folderId,name))
                    .setPageSize(PAGE_SIZE).setOrderBy("modifiedTime desc").setFields("files(" + IMAGE_FIELDS + ")")
                    .execute();

            return fileList.getFiles().stream().filter(file -> file.getMimeType().contains(IMAGE))
                    .map(this::buildAssetFrom).findFirst();
        });
    }

    @Override
    public PagedResult<Asset> listByFolder(String folderId,Integer pageSize,String nextPageToken){

        return execute(() -> {
            log.debug("Listing drive: {} with pageSize: {} and pageToken {}",folderId,pageSize,nextPageToken);
            final var driveFileList = drive.files().list().setQ(String.format("'%s' in parents",folderId))
                    .setPageSize(pageSize).setFields(String.format("nextPageToken, files(%s)",IMAGE_FIELDS));
            if (nextPageToken != null) {
                driveFileList.setPageToken(nextPageToken);
            }
            final var fileList = driveFileList.execute();
            return PagedResult.<Asset>builder()
                    .items(fileList.getFiles().stream().filter(file -> file.getMimeType().contains(IMAGE))
                            .map(this::buildAssetFrom).collect(Collectors.toList()))
                    .nextPageToken(fileList.getNextPageToken()).build();
        });

    }

    @Override
    public Asset create(AssetCreationRequest request){
        return getBy(request.folderId(),request.name()).orElseGet(() -> {
            var metadata = buildFileMetadata(request.name(),request.description(),request.folderId(),
                    stripExtension(request.multipartFile().getOriginalFilename()));
            return uploadFile(metadata,request.multipartFile());
        });
    }

    @Override
    public AssetFolder createFolderIn(String folderId,String name){
        return getFolderBy(folderId,name).orElseGet(() -> execute(() -> {
            log.debug("Creating folder with folderId: {} and name: {}",folderId,name);
            final var folder = new File();
            folder.setName(name);
            folder.setParents(List.of(folderId));
            folder.setMimeType(FOLDER_MIME_TYPE);

            final var createdFolder = drive.files().create(folder).setFields("id,name").execute();

            log.debug("Drive folder with folderId: {} and name: {} created successfully!",folderId,name);

            return AssetFolder.builder().folderId(createdFolder.getId()).name(createdFolder.getName()).build();
        }));
    }

    @Override
    public Optional<AssetFolder> getFolderBy(final String folderId,final String name){
        return execute(() -> {
            log.debug("Getting folder by folderId: {} and name: {}",folderId,name);
            final var driveFileList = drive.files().list().setQ(String
                    .format("mimeType = '%s'  and name = '%s' and '%s' in parents",FOLDER_MIME_TYPE,name,folderId))
                    .setFields("files(id,name)").execute();

            if (driveFileList.getFiles().isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(AssetFolder.builder().folderId(driveFileList.getFiles().getFirst().getId())
                    .name(driveFileList.getFiles().getFirst().getName()).build());
        });
    }

    @Override
    public Asset update(final AssetUpdateRequest request){

        final var metadata = buildFileMetadata(request.name(),request.description(),null,
                request.multipartFile() != null ? request.multipartFile().getOriginalFilename() : null);
        return uploadFile(metadata,request.multipartFile());
    }

    @Override
    public void delete(final String id){
        executeVoid(() -> drive.files().delete(id).execute());
    }

    private <T> T execute(IOCallable<T> action){
        try {
            return action.call();
        } catch (IOException e) {
            throw new AssetsBusinessException(AssetsErrorCode.ASSET_INPUT_OUTPUT_ERROR, e);
        }
    }

    private void executeVoid(IORunnable action){
        try {
            action.run();
        } catch (IOException e) {
            throw new AssetsBusinessException(AssetsErrorCode.ASSET_INPUT_OUTPUT_ERROR, e);
        }
    }

    private File buildFileMetadata(String name,String description,String parentId,String fallbackName){
        var file = new File();
        file.setName(name);
        file.setDescription(description != null ? description : fallbackName);
        if (parentId != null) {
            file.setParents(List.of(parentId));
        }
        return file;
    }

    private Asset uploadFile(File metadata,MultipartFile filePart){
        return execute(() -> {
            log.debug("Uploading file: {} with parentId: {}",metadata.getName(),metadata.getParents());
            var mediaContent = filePart != null
                    ? new InputStreamContent(filePart.getContentType(), filePart.getInputStream())
                    : null;

            var uploaded = drive.files().create(metadata,mediaContent).setFields(IMAGE_FIELDS)
                    .setSupportsAllDrives(true).execute();
            return buildAssetFrom(uploaded);
        });
    }

    private Asset buildAssetFrom(final File file){
        return Asset.builder().id(file.getId()).name(file.getName()).description(file.getDescription())
                .folderId(hasParents(file) ? file.getParents().getFirst() : null).mimeType(file.getMimeType())
                .height(file.getImageMediaMetadata() != null ? file.getImageMediaMetadata().getHeight() : null)
                .width(file.getImageMediaMetadata() != null ? file.getImageMediaMetadata().getWidth() : null)
                .thumbnail(file.getThumbnailLink()).original(file.getWebContentLink()).build();
    }

    private boolean hasParents(final File file){
        return file.getParents() != null && !file.getParents().isEmpty();
    }

    private String stripExtension(final String fileName){
        return (fileName != null && fileName.contains("."))
                ? fileName.substring(0,fileName.lastIndexOf("."))
                : fileName;
    }

    @FunctionalInterface
    private interface IOCallable<T> {
        T call() throws IOException;
    }

    @FunctionalInterface
    private interface IORunnable {
        void run() throws IOException;
    }
}
