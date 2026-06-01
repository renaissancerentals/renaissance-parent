package com.renaissancerentals.api.repository;

import com.renaissancerentals.api.domain.Sublet;
import com.renaissancerentals.api.domain.mapper.SubletMapper;
import com.renaissancerentals.api.error.NotFoundException;
import com.renaissancerentals.api.messaging.SubletRequest;
import com.renaissancerentals.assets.model.Asset;
import com.renaissancerentals.assets.model.AssetCreationRequest;
import com.renaissancerentals.assets.service.AssetService;
import com.renaissancerentals.persistence.dao.SubletDao;
import com.renaissancerentals.persistence.entity.SubletEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Repository
@RequiredArgsConstructor
@Slf4j
public class SubletRepository {
    private static final String SUBLET_PHOTOS_FOLDER = "photos";
    private final AssetService assetService;

    private final SubletDao subletDao;

    private final SubletMapper subletMapper;

    @Value("${renaissancerentals.sublet.folder}")
    private String subletFolderId;

    public List<Sublet> getAll() {
        return subletDao.findByActiveTrueAndApprovedTrue().stream()
                .map(subletMapper::fromEntity)
                .toList();
    }

    public Optional<Sublet> getSublet(String assetKey) {
        return subletDao.findOneByAssetKey(assetKey).map(subletMapper::fromEntity);
    }

    @Transactional
    public Sublet createSublet(SubletRequest sublet) {
        var subletEntity = subletMapper.toEntity(sublet);
        addDefaultFields(subletEntity);
        return createSubletImageFolders(subletDao.save(subletEntity));
    }

    @Transactional
    public Asset createAsset(String assetKey, MultipartFile file, String name, Boolean isCoverImage) {
        var sublet =
                subletDao.findOneByAssetKey(assetKey).orElseThrow(() -> new NotFoundException("Sublet not found!"));
        if (isCoverImage) {
            var asset = assetService.create(AssetCreationRequest.builder()
                    .folderId(sublet.getSubletFolderId())
                    .multipartFile(file)
                    .name(name)
                    .build());
            sublet.setCoverImage(asset.original());
            subletDao.save(sublet);
            return asset;
        } else {
            return assetService.create(AssetCreationRequest.builder()
                    .folderId(sublet.getPhotosFolderId())
                    .multipartFile(file)
                    .name(name)
                    .build());
        }
    }

    @Transactional
    public void deactivateSubletBy(String assetKey) {
        var sublet =
                subletDao.findOneByAssetKey(assetKey).orElseThrow(() -> new NotFoundException("Sublet not found!"));

        deactivateSublet(sublet);
    }

    private void deactivateSublet(SubletEntity sublet) {
        log.debug("Deactivating sublet: {}", sublet.getId());
        sublet.setActive(false);
        subletDao.save(sublet);
    }

    private Sublet createSubletImageFolders(SubletEntity subletEntity) {

        var subletFolder = assetService.createFolderIn(subletFolderId, String.valueOf(subletEntity.getId()));
        subletEntity.setSubletFolderId(subletFolder.folderId());
        var photosFolder = assetService.createFolderIn(subletFolder.folderId(), SUBLET_PHOTOS_FOLDER);
        subletEntity.setPhotosFolderId(photosFolder.folderId());
        var entity = subletDao.save(subletEntity);
        return subletMapper.fromEntity(entity);
    }

    private void addDefaultFields(SubletEntity subletEntity) {
        subletEntity.setAssetKey(UUID.randomUUID().toString());
        subletEntity.setActive(true);
    }
}
