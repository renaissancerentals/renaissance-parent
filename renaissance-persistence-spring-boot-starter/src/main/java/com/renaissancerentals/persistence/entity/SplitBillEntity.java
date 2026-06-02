package com.renaissancerentals.persistence.entity;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

@Table("split_bill")
@Builder
public record SplitBillEntity(@Id Long id, OwnerData ownerData, @LastModifiedDate LocalDateTime lastModifiedDate) {

    public record OwnerData(List<Owner> owners) {}

    public record Owner(String name, int unitCount, List<OwnerClass> classes) {
        public int effectiveUnitCount() {
            return classes == null || classes.isEmpty()
                    ? unitCount
                    : classes.stream().mapToInt(OwnerClass::unitCount).sum();
        }
    }

    public record OwnerClass(String name, int unitCount) {}
}
