package com.renaissancerentals.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.renaissancerentals.api.domain.Faq;
import com.renaissancerentals.api.service.FaqService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/faqs")
@RequiredArgsConstructor
public class FaqController {

    private final FaqService faqService;

    @GetMapping("/maintenance")
    public ResponseEntity<List<Faq>> getMaintenanceFaqs(){
        return ResponseEntity.ok(faqService.getMaintenanceFaqs());
    }

    @GetMapping("/resident")
    public ResponseEntity<List<Faq>> getResidentFaqs(){
        return ResponseEntity.ok(faqService.getResidentFaqs());
    }
}
