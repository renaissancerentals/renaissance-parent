package com.renaissancerentals.data.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.renaissancerentals.data.domain.data.Sublet;
import com.renaissancerentals.data.domain.repository.SubletRepository;
import com.renaissancerentals.data.error.NotFoundException;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sublets")
@RequiredArgsConstructor
public class SubletController {

    private final SubletRepository subletRepository;

    @GetMapping("/{subletId}")
    public ResponseEntity<Sublet> getSublet(@PathVariable("subletId") Long subletId){
        return ResponseEntity.ok(subletRepository.getSublet(subletId)
                .orElseThrow(() -> new NotFoundException(String.format("Sublet with id: %d",subletId))));
    }

    @GetMapping
    public ResponseEntity<List<Sublet>> getActiveAndApprovedSublets(){
        return ResponseEntity.ok(subletRepository.getActiveAndApprovedSublets());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Sublet>> getActiveSublets(){
        return ResponseEntity.ok(subletRepository.getActiveSublets());
    }

}
