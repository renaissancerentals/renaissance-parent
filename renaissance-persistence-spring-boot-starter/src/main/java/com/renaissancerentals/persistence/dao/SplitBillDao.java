package com.renaissancerentals.persistence.dao;

import com.renaissancerentals.persistence.entity.SplitBillEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SplitBillDao extends CrudRepository<SplitBillEntity, Long> {

    @Modifying
    @Query(
            """
            INSERT INTO split_bill (owner_data, last_modified_date)
            VALUES (:ownerData::jsonb, CURRENT_TIMESTAMP)
            """)
    void insert(@Param("ownerData") String ownerData);

    @Modifying
    @Query(
            """
            UPDATE split_bill
            SET owner_data         = :ownerData::jsonb,
                last_modified_date = CURRENT_TIMESTAMP
            WHERE id = :id
            """)
    void update(@Param("id") Long id, @Param("ownerData") String ownerData);
}
