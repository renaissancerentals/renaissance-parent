package com.renaissancerentals.foundation.text.data;

import lombok.Builder;

@Builder
public record TextMessage(String from, String to, String message) {}
