package com.renaissancerentals.persistence.dao;

import com.renaissancerentals.persistence.entity.UtilityEntity;
import java.util.List;
import java.util.Set;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface UtilityDao extends CrudRepository<UtilityEntity, Long> {

    List<UtilityEntity> findByFloorplanIdAndNameAndType(
            @Param("floorplanId") String floorplanId, @Param("name") String name, @Param("type") String type);

    void deleteByFloorplanId(@Param("floorplanId") String floorplanId);

    List<UtilityEntity> findByFloorplanId(@Param("floorplanId") String floorplanId);

    @Query("SELECT DISTINCT name FROM utility")
    Set<String> findDistinctNames();
}
