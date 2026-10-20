package com.renaissancerentals.assets.controller;

import com.renaissancerentals.assets.error.AssetsNotFoundException;
import com.renaissancerentals.assets.model.Asset;
import com.renaissancerentals.assets.model.PagedResult;
import com.renaissancerentals.assets.service.AssetService;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@CrossOrigin
@RequestMapping(value = "/api", produces = "application/json")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @GetMapping("/folders/{folderId}/assets")
    public ResponseEntity<PagedResult<Asset>> getAssetsByFolder(
            @PathVariable("folderId") String folderId,
            @RequestParam(name = "pageSize") Optional<Integer> pageSize,
            @RequestParam(name = "nextPageToken") Optional<String> nextPageToken) {

        return ResponseEntity.ok(
                assetService.listByFolder(folderId, pageSize.orElse(null), nextPageToken.orElse(null)));
    }

    @GetMapping("/assets/{id}")
    public ResponseEntity<Asset> getAssetById(@PathVariable("id") String id) {
        return ResponseEntity.ok(assetService
                .get(id)
                .orElseThrow(() -> new AssetsNotFoundException("Asset with Id: " + id + " not found")));
    }

    /**
     * Nothing is kept on the server. This only lets browsers and proxies keep a photo for a day, so a returning visitor
     * does not make the server download it from Drive again.
     */
    private static final CacheControl DOWNLOAD_CACHE =
            CacheControl.maxAge(Duration.ofDays(1)).cachePublic();

    @GetMapping("/assets/{id}/download")
    public ResponseEntity<StreamingResponseBody> getFile(@PathVariable("id") String id) {

        // sanitize for header safety
        var safeFilename = StringUtils.cleanPath(id).replaceAll("[\r\n]", "");

        // Asked before the first byte is sent, so a missing file is still a clean 404. The file itself is piped from
        // Drive to the client and never held in memory whole, however many photos are being fetched at once.
        assetService.get(id).orElseThrow(() -> new AssetsNotFoundException("Asset with Id: " + id + " not found"));

        StreamingResponseBody body = out -> assetService.downloadTo(id, out);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .cacheControl(DOWNLOAD_CACHE)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(safeFilename, StandardCharsets.UTF_8)
                                .build()
                                .toString())
                .body(body);
    }
}
