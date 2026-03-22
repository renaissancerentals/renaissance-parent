package com.renaissancerentals.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.renaissancerentals.foundation.error.ErrorMessage;
import com.renaissancerentals.foundation.error.ServerException;

@RestController
public class TestController {

    @GetMapping("/api/test")
    public ResponseEntity<String> test(){
        throw new ServerException(ErrorMessage.builder().message("Test Server Error").build());

    }
}
