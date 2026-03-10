package com.renaissancerentals.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.renaissancerentals.api.messaging.UtilityConfirmationRequest;
import com.renaissancerentals.api.service.UtilityConfirmationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/utilities")
@RequiredArgsConstructor
public class UtilityController {

    private final UtilityConfirmationService utilityConfirmationService;

    @PostMapping("/confirmation")
    public ResponseEntity<Void> postUtilityConfirmationRequest(
            @RequestBody @Valid UtilityConfirmationRequest utilityConfirmation){

        utilityConfirmationService.save(utilityConfirmation);

        return ResponseEntity.ok().build();
    }

}
