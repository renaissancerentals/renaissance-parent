package com.renaissancerentals.api.domain;

import lombok.Builder;

@Builder
public record Utility(String name, String type, Float averageMonthlyBill) {
}
