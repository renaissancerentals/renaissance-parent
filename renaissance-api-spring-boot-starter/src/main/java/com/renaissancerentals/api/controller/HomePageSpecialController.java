package com.renaissancerentals.api.controller;

import com.renaissancerentals.api.domain.HomePageSpecial;
import com.renaissancerentals.api.service.HomePageSpecialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/homePageSpecials")
@RequiredArgsConstructor
public class HomePageSpecialController {

    private final HomePageSpecialService homePageSpecialService;

    @GetMapping
    public ResponseEntity<List<HomePageSpecial>> getAll() {
        return ResponseEntity.ok(homePageSpecialService.getHomePageSpecials());
    }
}
