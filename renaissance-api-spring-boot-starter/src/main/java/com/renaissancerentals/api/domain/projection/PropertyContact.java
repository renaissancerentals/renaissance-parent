package com.renaissancerentals.api.domain.projection;

import lombok.Builder;

@Builder
public record PropertyContact(String propertyName, String email, String secondaryEmail, String phone) {

}
