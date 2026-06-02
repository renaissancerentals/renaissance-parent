package com.renaissancerentals.persistence.entity;

import java.util.List;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("split_bill")
@Builder
public record SplitBillEntity(@Id Long id, OwnerData ownerData) {

    public record OwnerData(String name, int unitCount, List<OwnerClassData> classes) {

        public int effectiveUnitCount() {
            return classes == null || classes.isEmpty()
                    ? unitCount
                    : classes.stream().mapToInt(OwnerClassData::unitCount).sum();
        }
    }

    public record OwnerClassData(String name, int unitCount) {}
}
