package com.renaissancerentals.persistence.dao;

import com.renaissancerentals.persistence.entity.ShortTermFloorplanEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ShortTermFloorplanDao extends CrudRepository<ShortTermFloorplanEntity, Long> {

    Optional<ShortTermFloorplanEntity> findOneByFloorplanId(@Param("floorplanId") String floorplanId);

    @Query(
            value = "SELECT s.* from short_term_floorplan s join floorplan f on s.floorplan_id=f.id\n"
                    + "join property p on f.property_id=p.id where p.id=:propertyId")
    List<ShortTermFloorplanEntity> findByPropertyId(@Param("propertyId") String propertyId);
}
