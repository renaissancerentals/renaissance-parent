package com.renaissancerentals.assets.service;

import java.util.Optional;

import com.renaissancerentals.assets.model.PagedResult;
import com.renaissancerentals.assets.model.video.InitiateUploadRequest;
import com.renaissancerentals.assets.model.video.VideoAsset;
import com.renaissancerentals.assets.model.video.VideoUploadMetadata;

public interface VideoService {
    int PAGE_SIZE = 10;

    /**
     * Fetches the video with the given ID.
     *
     * @param id
     *            the video ID
     * @return an Optional containing the video if found
     */
    Optional<VideoAsset> get(final String id);

    /**
     * Fetches the video with the given name in the specified folder.
     *
     * @param folderId
     *            of the video
     * @param name
     *            of the video to be fetched
     * @return an Optional containing the video if found
     */

    Optional<VideoAsset> getBy(final String folderId,final String name);

    /**
     * Lists videos under the specified folder with pagination support.
     *
     * @param folderId
     *            the folder ID
     * @param pageSize
     *            the number of items per page
     * @param nextPageToken
     *            the token to fetch the next page
     * @return a paged result of assets
     */
    PagedResult<VideoAsset> listByFolder(final String folderId,final Integer pageSize,final String nextPageToken);

    /**
     * Lists videos under the specified folder with pagination support.
     *
     * @param folderId
     *            the folder ID
     * @return a paged result of assets
     */
    default PagedResult<VideoAsset> listByFolder(final String folderId){
        return listByFolder(folderId,PAGE_SIZE,null);
    }

    /**
     * Initiates a video upload session.
     *
     * @param initiateUploadRequest
     *            initiate upload request parameter
     * @return VideoUploadMetadata object containing upload session information
     */

    VideoUploadMetadata initiateUpload(final InitiateUploadRequest initiateUploadRequest);

    /**
     * Deletes the video with the given ID.
     *
     * @param id
     *            the ID of the asset to be deleted
     */
    void delete(final String id);

}
