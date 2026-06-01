package com.renaissancerentals.api.domain;

import lombok.Builder;

@Builder
public record Utility(Long id, String name, String type, Float averageMonthlyBill) {}
