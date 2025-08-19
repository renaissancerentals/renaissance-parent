package com.renaissancerentals.data.domain.repository;

import com.renaissancerentals.data.domain.data.Sublet;

import java.util.List;
import java.util.Optional;

public interface SubletRepository {
    Optional<Sublet> getSublet(Long subletId);

    List<Sublet> getActiveAndApprovedSublets();

    List<Sublet> getActiveSublets();
}
