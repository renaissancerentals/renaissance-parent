package com.renaissancerentals.persistence.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renaissancerentals.persistence.entity.SplitBillEntity;
import com.renaissancerentals.persistence.support.PostgresIntegrationTest;
import java.util.List;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Covers the one hand-written write path in this module: an INSERT/UPDATE using
 * the Postgres-specific `::jsonb` cast, read back through the PGobject-based
 * OwnerDataConverters. This is the query most likely to break silently on a
 * Postgres major-version bump, since it depends on pgjdbc's JSONB wire handling
 * rather than plain ANSI SQL.
 */
class SplitBillDaoIntegrationTest extends PostgresIntegrationTest {

    @Autowired
    private SplitBillDao splitBillDao;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void insertAndUpdateRoundTripOwnerDataThroughJsonb() throws Exception {
        var initialOwnerData = new SplitBillEntity.OwnerData(
                List.of(new SplitBillEntity.Owner("Alice", 2, List.of(new SplitBillEntity.OwnerClass("A", 2)))));

        splitBillDao.insert(objectMapper.writeValueAsString(initialOwnerData));

        SplitBillEntity inserted = onlyEntity();
        assertThat(inserted.ownerData().owners()).hasSize(1);
        assertThat(inserted.ownerData().owners().getFirst().name()).isEqualTo("Alice");
        assertThat(inserted.ownerData().owners().getFirst().effectiveUnitCount())
                .isEqualTo(2);

        var updatedOwnerData = new SplitBillEntity.OwnerData(List.of(
                new SplitBillEntity.Owner("Alice", 2, List.of(new SplitBillEntity.OwnerClass("A", 2))),
                new SplitBillEntity.Owner("Bob", 1, List.of())));

        splitBillDao.update(inserted.id(), objectMapper.writeValueAsString(updatedOwnerData));

        SplitBillEntity updated = onlyEntity();
        assertThat(updated.id()).isEqualTo(inserted.id());
        assertThat(updated.ownerData().owners()).hasSize(2);
        assertThat(updated.ownerData().owners().get(1).name()).isEqualTo("Bob");
        assertThat(updated.ownerData().owners().get(1).effectiveUnitCount()).isEqualTo(1);
    }

    private SplitBillEntity onlyEntity() {
        List<SplitBillEntity> all = StreamSupport.stream(splitBillDao.findAll().spliterator(), false)
                .toList();
        assertThat(all).hasSize(1);
        return all.getFirst();
    }
}
