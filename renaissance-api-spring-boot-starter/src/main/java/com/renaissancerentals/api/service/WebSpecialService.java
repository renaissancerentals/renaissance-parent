package com.renaissancerentals.api.service;

import com.renaissancerentals.api.domain.WebSpecial;
import com.renaissancerentals.api.domain.mapper.WebSpecialMapper;
import com.renaissancerentals.persistence.dao.WebSpecialDao;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSpecialService {

    private final WebSpecialDao webSpecialDao;
    private final WebSpecialMapper webSpecialMapper;
    private final ExecutorService virtualThreadExecutor;

    public CompletableFuture<List<WebSpecial>> getWebSpecialForFloorplanAsync(String floorplanId) {

        return CompletableFuture.supplyAsync(
                () -> webSpecialDao.findActiveByFloorplanId(floorplanId).stream()
                        .map(webSpecialMapper::toDomain)
                        .toList(),
                virtualThreadExecutor);
    }
}
