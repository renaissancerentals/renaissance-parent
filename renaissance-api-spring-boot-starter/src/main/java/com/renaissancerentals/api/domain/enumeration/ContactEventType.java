package com.renaissancerentals.api.domain.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ContactEventType {
    CLICKED("clicked"), SUBMITTED("submitted"), INITIATED("initiated");

    private final String value;

    ContactEventType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue(){
        return value;
    }

    @JsonCreator
    public static ContactEventType fromValue(String value){
        for (ContactEventType eventType : ContactEventType.values()) {
            if (eventType.value.equals(value)) {
                return eventType;
            }
        }
        throw new IllegalArgumentException("No enum const ContactEventType found for value " + value);
    }
}
