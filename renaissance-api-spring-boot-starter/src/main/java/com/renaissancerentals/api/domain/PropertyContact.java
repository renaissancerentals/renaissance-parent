package com.renaissancerentals.api.domain;

import lombok.Builder;

@Builder
public record PropertyContact(String propertyName, String email, String secondaryEmail, String phone) {

}
