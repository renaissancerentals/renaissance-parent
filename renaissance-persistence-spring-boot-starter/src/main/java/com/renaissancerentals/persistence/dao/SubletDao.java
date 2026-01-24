package com.renaissancerentals.persistence.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.renaissancerentals.persistence.entity.SubletEntity;

public interface SubletDao extends CrudRepository<SubletEntity, Long> {

    List<SubletEntity> findByActiveTrue();

    List<SubletEntity> findByActiveTrueAndApprovedTrue();

    Optional<SubletEntity> findOneByAssetKey(@Param("assetKey") String assetKey);

    @Query("SELECT * FROM sublet WHERE cover_image = ''")
    List<SubletEntity> findByCoverImageEmpty();

    @Query("SELECT * FROM sublet WHERE available_to <= :availableTo OR created_date<=:createdDate")
    List<SubletEntity> findExpired(@Param("availableTo") LocalDate availableTo,
            @Param("createdDate") LocalDateTime createdDate);

    @Query("SELECT * FROM sublet WHERE active = false AND created_date<=:createdDate")
    List<SubletEntity> findExpiredInactive(@Param("createdDate") LocalDateTime createdDate);
}
