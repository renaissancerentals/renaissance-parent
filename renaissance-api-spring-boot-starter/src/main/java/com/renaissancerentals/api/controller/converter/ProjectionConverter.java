package com.renaissancerentals.api.controller.converter;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.renaissancerentals.api.domain.projection.Projection;

@Component
public class ProjectionConverter implements Converter<String, Projection> {

    @Override
    public Projection convert(@NotNull String source){
        return Projection.fromValue(source);
    }
}
