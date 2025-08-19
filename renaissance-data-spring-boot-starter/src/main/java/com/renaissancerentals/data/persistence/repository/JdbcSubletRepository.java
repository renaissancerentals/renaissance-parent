package com.renaissancerentals.data.persistence.repository;

import com.renaissancerentals.data.domain.data.Sublet;
import com.renaissancerentals.data.domain.mapper.SubletMapper;
import com.renaissancerentals.data.domain.repository.SubletRepository;
import com.renaissancerentals.data.persistence.dao.SubletDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class JdbcSubletRepository implements SubletRepository {
    private final SubletDao subletDao;
    private final SubletMapper subletMapper;

    @Override
    public Optional<Sublet> getSublet(Long subletId) {
        return subletDao.findById(subletId).map(subletMapper::toDomain);
    }

    @Override
    public List<Sublet> getActiveAndApprovedSublets() {
        return subletDao.findByActiveTrueAndApprovedTrue().stream().map(subletMapper::toDomain).toList();
    }

    @Override
    public List<Sublet> getActiveSublets() {
        return subletDao.findByActiveTrue().stream().map(subletMapper::toDomain).toList();
    }
}
