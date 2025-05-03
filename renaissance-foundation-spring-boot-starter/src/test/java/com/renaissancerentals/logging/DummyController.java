package com.renaissancerentals.logging;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dummy")
class DummyController {
    @PostMapping(value = "/logs", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, String>> test(@RequestBody Map<String, String> body){
        return ResponseEntity.ok(body);
    }
}
