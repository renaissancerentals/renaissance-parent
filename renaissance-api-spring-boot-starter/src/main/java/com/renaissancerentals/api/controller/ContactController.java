package com.renaissancerentals.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.renaissancerentals.api.messaging.ContactMessageRequest;
import com.renaissancerentals.api.service.ContactService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @PostMapping
    public ResponseEntity<Void> postContact(@RequestBody @Valid ContactMessageRequest contactMessage){

        contactService.save(contactMessage);

        return ResponseEntity.ok().build();
    }

}
