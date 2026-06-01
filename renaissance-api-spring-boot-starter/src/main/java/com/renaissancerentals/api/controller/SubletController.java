package com.renaissancerentals.api.controller;

import com.renaissancerentals.api.domain.Sublet;
import com.renaissancerentals.api.error.NotFoundException;
import com.renaissancerentals.api.messaging.SubletMessageRequest;
import com.renaissancerentals.api.messaging.SubletRequest;
import com.renaissancerentals.api.repository.SubletRepository;
import com.renaissancerentals.api.service.SubletMessageService;
import com.renaissancerentals.assets.model.Asset;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/sublets")
@RequiredArgsConstructor
public class SubletController {

    private final SubletMessageService subletMessageService;
    private final SubletRepository subletRepository;

    @PostMapping
    @Transactional
    public Sublet create(@Valid @RequestBody SubletRequest subletRequest) {
        var sublet = subletRepository.createSublet(subletRequest);
        subletMessageService.sendNewSubletAlert(sublet);
        return sublet;
    }

    @GetMapping
    public ResponseEntity<List<Sublet>> getAll() {
        return ResponseEntity.ok(subletRepository.getAll());
    }

    @GetMapping("/{assetKey}")
    public ResponseEntity<Sublet> get(@PathVariable("assetKey") String assetKey) {

        return ResponseEntity.ok(subletRepository
                .getSublet(assetKey)
                .orElseThrow(() -> new NotFoundException(String.format("Asset Key %s not found", assetKey))));
    }

    @PostMapping("/{assetKey}/assets")
    public ResponseEntity<Asset> postAsset(
            @PathVariable("assetKey") String assetKey,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("isCoverImage") Boolean isCoverImage) {

        return ResponseEntity.ok(subletRepository.createAsset(assetKey, file, name, isCoverImage));
    }

    @DeleteMapping("/{assetKey}")
    public ResponseEntity<Void> delete(@PathVariable("assetKey") String assetKey) {
        subletRepository.deactivateSubletBy(assetKey);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{assetKey}/messages")
    public ResponseEntity<Void> sendMessage(
            @PathVariable("assetKey") String assetKey, @Valid @RequestBody SubletMessageRequest subletMessage) {

        var sublet = subletRepository.getSublet(assetKey).orElseThrow(() -> new NotFoundException("Sublet not found!"));
        subletMessageService.sendMessage(sublet, subletMessage);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
