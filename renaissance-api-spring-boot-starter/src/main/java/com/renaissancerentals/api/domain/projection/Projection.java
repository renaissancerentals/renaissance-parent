package com.renaissancerentals.api.domain.projection;

import java.util.Arrays;

import com.renaissancerentals.foundation.error.ClientException;
import com.renaissancerentals.foundation.error.ErrorMessage;

import lombok.Getter;

@Getter
public enum Projection {

    ADDRESS("address"), DETAILS("details"), FILTER("filter"), SPOTLIGHT("spotlight"), UTILITIES("utilities"), ENRICHED(
            "enriched");

    private final String value;

    Projection(String value) {
        this.value = value;
    }

    public static Projection fromValue(String value){
        return Arrays.stream(Projection.values()).filter(p -> p.value.equalsIgnoreCase(value)).findFirst().orElseThrow(
                () -> new ClientException(ErrorMessage.builder().message("Unsupported Projection").build()));
    }

}
