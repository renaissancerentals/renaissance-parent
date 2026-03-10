package com.renaissancerentals.api.service;

import com.renaissancerentals.api.domain.HomePageSpecial;
import com.renaissancerentals.api.domain.mapper.HomePageSpecialMapper;
import com.renaissancerentals.persistence.dao.HomePageSpecialDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomePageSpecialService {
    private final HomePageSpecialDao homePageSpecialDao;
    private final HomePageSpecialMapper homePageSpecialMapper;

    public List<HomePageSpecial> getHomePageSpecials() {
        return homePageSpecialDao.findAllActive().stream().map(homePageSpecialMapper::toDomain).toList();
    }
}
