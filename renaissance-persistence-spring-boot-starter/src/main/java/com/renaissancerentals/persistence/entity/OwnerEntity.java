package com.renaissancerentals.persistence.entity;

import java.util.Set;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

@Table("owner")
@Builder
public record OwnerEntity(
        @Id Long id,
        String name,
        int unitCount,
        @MappedCollection(idColumn = "owner_id") Set<OwnerClassEntity> classes) {

    public OwnerEntity {
        classes = classes == null ? Set.of() : Set.copyOf(classes);
    }
}
