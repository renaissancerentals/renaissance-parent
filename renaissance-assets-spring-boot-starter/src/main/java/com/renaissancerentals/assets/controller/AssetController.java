package com.renaissancerentals.assets.controller;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.renaissancerentals.assets.error.AssetsNotFoundException;
import com.renaissancerentals.assets.model.Asset;
import com.renaissancerentals.assets.model.PagedResult;
import com.renaissancerentals.assets.service.AssetService;

@RestController
@CrossOrigin
@RequestMapping(value = "/api", produces = "application/json")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @GetMapping("/folders/{folderId}/assets")
    public ResponseEntity<PagedResult<Asset>> getAssetsByFolder(@PathVariable("folderId") String folderId,
            @RequestParam(name = "pageSize") Optional<Integer> pageSize,
            @RequestParam(name = "nextPageToken") Optional<String> nextPageToken){

        return ResponseEntity.ok(assetService.listByFolder(folderId,pageSize.orElse(null),nextPageToken.orElse(null)));
    }

    @GetMapping("/assets/{id}")
    public ResponseEntity<Asset> getAssetById(@PathVariable("id") String id){
        return ResponseEntity.ok(assetService.get(id)
                .orElseThrow(() -> new AssetsNotFoundException("Asset with Id: " + id + " not found")));
    }

    @GetMapping("/assets/{id}/download")
    public ResponseEntity<Resource> getFile(@PathVariable("id") String id){

        // sanitize for header safety
        var safeFilename = StringUtils.cleanPath(id).replaceAll("[\r\n]","");

        final var fileBytes = assetService.getFile(id)
                .orElseThrow(() -> new AssetsNotFoundException("Asset with Id: " + id + " not found"));

        final var resource = new ByteArrayResource(fileBytes);

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resource.contentLength()).header(HttpHeaders.CONTENT_DISPOSITION,ContentDisposition
                        .attachment().filename(safeFilename,StandardCharsets.UTF_8).build().toString())
                .body(resource);
    }

}
