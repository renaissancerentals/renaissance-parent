package com.renaissancerentals.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("owner_class")
public record OwnerClassEntity(@Id Long id, String name, int unitCount) {}
