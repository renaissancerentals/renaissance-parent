package com.renaissancerentals.assets.service;

import com.renaissancerentals.assets.model.*;
import java.util.Optional;
import org.springframework.web.util.UriComponentsBuilder;

public interface AssetService {

    int PAGE_SIZE = 10;

    /**
     * Fetches the asset with the given ID.
     *
     * @param id
     *            the asset ID
     * @return an Optional containing the asset if found
     */
    Optional<Asset> get(final String id);

    /**
     * Fetches the asset file with the given ID.
     *
     * @param id
     *            the asset ID of the file to be fetched.
     * @return an Optional containing the asset file if found.
     */
    Optional<byte[]> getFile(final String id);

    /**
     * Extracts the 'id' query parameter from the given URL and fetches the corresponding asset.
     *
     * @param url
     *            the asset URL containing query parameters
     * @return an Optional of the found asset, or empty if 'id' param is missing or not found
     */
    default Optional<Asset> getFromUrl(final String url) {
        return Optional.ofNullable(UriComponentsBuilder.fromUriString(url)
                        .build()
                        .getQueryParams()
                        .getFirst("id"))
                .flatMap(this::get);
    }

    /**
     * Fetches the asset with the given name in the specified folder.
     *
     * @param folderId
     *            of the asset
     * @param name
     *            of the asset to be fetched
     * @return an Optional containing the asset if found
     */
    Optional<Asset> getBy(final String folderId, final String name);

    /**
     * Lists assets under the specified folder with pagination support.
     *
     * @param folderId
     *            the folder ID
     * @param pageSize
     *            the number of items per page
     * @param nextPageToken
     *            the token to fetch the next page
     * @return a paged result of assets
     */
    PagedResult<Asset> listByFolder(final String folderId, final Integer pageSize, final String nextPageToken);

    /**
     * Lists assets under the specified folder with pagination support.
     *
     * @param folderId
     *            the folder ID
     * @return a paged result of assets
     */
    default PagedResult<Asset> listByFolder(final String folderId) {
        return listByFolder(folderId, PAGE_SIZE, null);
    }

    /**
     * Creates a new asset using the provided request object.
     *
     * @param assetCreationRequest
     *            the details needed to create the asset, including folder ID, file, name, and optional description
     * @return the created Asset instance
     */
    Asset create(final AssetCreationRequest assetCreationRequest);

    /**
     * Creates a new folder in the specified folder.
     *
     * @param folderId
     *            parent folder ID
     * @param name
     *            name of the folder to be created
     * @return AssetFolder object containing folder ID and name
     */
    AssetFolder createFolderIn(final String folderId, final String name);

    Optional<AssetFolder> getFolderBy(final String folderId, final String name);

    /**
     * Updates an existing asset using the provided request object.
     *
     * @param assetUpdateRequest
     *            the update request containing asset ID and updated fields
     * @return the updated Asset instance
     */
    Asset update(final AssetUpdateRequest assetUpdateRequest);

    /**
     * Deletes the asset with the given ID.
     *
     * @param id
     *            the ID of the asset to be deleted
     */
    void delete(final String id);
}
