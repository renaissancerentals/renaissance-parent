package com.renaissancerentals.data.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.renaissancerentals.data.domain.data.Sublet;
import com.renaissancerentals.data.domain.mapper.SubletMapper;
import com.renaissancerentals.persistence.dao.SubletDao;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class SubletRepository {
    private final SubletDao subletDao;
    private final SubletMapper subletMapper;

    public Optional<Sublet> getSublet(Long subletId){
        return subletDao.findById(subletId).map(subletMapper::toDomain);
    }

    public Optional<Sublet> getSubletByAssetKey(String assetKey){
        return subletDao.findOneByAssetKey(assetKey).map(subletMapper::toDomain);
    }

    public List<Sublet> getActiveAndApprovedSublets(){
        return subletDao.findByActiveTrueAndApprovedTrue().stream().map(subletMapper::toDomain).toList();
    }

    public List<Sublet> getActiveSublets(){
        return subletDao.findByActiveTrue().stream().map(subletMapper::toDomain).toList();
    }

}
