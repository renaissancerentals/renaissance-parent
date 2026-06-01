package com.renaissancerentals.assets.controller;

import com.renaissancerentals.assets.error.AssetsNotFoundException;
import com.renaissancerentals.assets.model.PagedResult;
import com.renaissancerentals.assets.model.video.VideoAsset;
import com.renaissancerentals.assets.service.VideoService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping(value = "/api", produces = "application/json")
@RequiredArgsConstructor
public class VideoAssetController {
    private final VideoService videoService;

    @GetMapping("/videos/{id}")
    public ResponseEntity<VideoAsset> get(@PathVariable("id") String id) {
        return ResponseEntity.ok(videoService
                .get(id)
                .orElseThrow(() -> new AssetsNotFoundException("Video with Id: " + id + " not found")));
    }

    @GetMapping("/folders/{folderId}/videos")
    public ResponseEntity<PagedResult<VideoAsset>> getByFolder(
            @PathVariable("folderId") String folderId,
            @RequestParam(name = "pageSize") Optional<Integer> pageSize,
            @RequestParam(name = "nextPageToken") Optional<String> nextPageToken) {

        return ResponseEntity.ok(
                videoService.listByFolder(folderId, pageSize.orElse(null), nextPageToken.orElse(null)));
    }

    @GetMapping("/folders/{folderId}/videos/by-name/{name}")
    public ResponseEntity<VideoAsset> getByFolderIdAndName(
            @PathVariable("folderId") String folderId, @PathVariable("name") String name) {
        return ResponseEntity.ok(videoService
                .getBy(folderId, name)
                .orElseThrow(() ->
                        new AssetsNotFoundException("Video with name: " + name + " not found in folder: " + folderId)));
    }
}
