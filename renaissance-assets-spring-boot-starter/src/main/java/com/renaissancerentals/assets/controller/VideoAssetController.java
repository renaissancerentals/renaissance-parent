package com.renaissancerentals.assets.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.renaissancerentals.assets.error.AssetsNotFoundException;
import com.renaissancerentals.assets.model.PagedResult;
import com.renaissancerentals.assets.model.video.VideoAsset;
import com.renaissancerentals.assets.service.VideoService;

import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin
@RequestMapping(value = "/api", produces = "application/json")
@RequiredArgsConstructor
public class VideoAssetController {
    private final VideoService videoService;

    @GetMapping("/videos/{id}")
    public ResponseEntity<VideoAsset> get(@PathVariable String id){
        return ResponseEntity.ok(videoService.get(id)
                .orElseThrow(() -> new AssetsNotFoundException("Video with Id: " + id + " not found")));
    }

    @GetMapping("/folders/{folderId}/videos")
    public ResponseEntity<PagedResult<VideoAsset>> getByFolder(@PathVariable String folderId,
            @RequestParam Optional<Integer> pageSize,@RequestParam Optional<String> nextPageToken){

        return ResponseEntity.ok(videoService.listByFolder(folderId,pageSize.orElse(null),nextPageToken.orElse(null)));
    }

    @GetMapping("/folders/{folderId}/videos/by-name/{name}")
    public ResponseEntity<VideoAsset> getByFolderIdAndName(@PathVariable String folderId,@PathVariable String name){
        return ResponseEntity.ok(videoService.getBy(folderId,name).orElseThrow(
                () -> new AssetsNotFoundException("Video with name: " + name + " not found in folder: " + folderId)));
    }
}
