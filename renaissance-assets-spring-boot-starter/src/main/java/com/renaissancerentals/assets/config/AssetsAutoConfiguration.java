package com.renaissancerentals.assets.config;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.renaissancerentals.assets.controller.AssetController;
import com.renaissancerentals.assets.controller.VideoAssetController;
import com.renaissancerentals.assets.external.GoogleDriveAssetAdapter;
import com.renaissancerentals.assets.external.GoogleDriveFactory;
import com.renaissancerentals.assets.external.GoogleDriveVideoAdapter;

@Configuration
@EnableConfigurationProperties(AssetsConfigProperties.class)
@ImportAutoConfiguration(classes = {AssetController.class, VideoAssetController.class, GoogleDriveAssetAdapter.class,
        GoogleDriveFactory.class, GoogleDriveVideoAdapter.class})
public class AssetsAutoConfiguration {

}
