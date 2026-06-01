package com.renaissancerentals.api.controller.converter;

import com.renaissancerentals.api.domain.projection.Projection;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ProjectionConverter implements Converter<String, Projection> {

    @Override
    public Projection convert(@NotNull String source) {
        return Projection.fromValue(source);
    }
}
