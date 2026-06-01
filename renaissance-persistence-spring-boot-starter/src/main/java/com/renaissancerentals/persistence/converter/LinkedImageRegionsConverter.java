package com.renaissancerentals.persistence.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renaissancerentals.persistence.entity.LinkedImageRegions;
import org.postgresql.util.PGobject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

public class LinkedImageRegionsConverter {
    @WritingConverter
    public record LinkedImageWritingConverter(ObjectMapper objectMapper)
            implements Converter<LinkedImageRegions, PGobject> {
        @Override
        public PGobject convert(LinkedImageRegions regions) {
            try {
                PGobject pgObject = new PGobject();
                pgObject.setType("jsonb");
                pgObject.setValue(objectMapper.writeValueAsString(regions));
                return pgObject;
            } catch (Exception e) {
                throw new RuntimeException("Failed to convert LinkedImageRegions to JSONB", e);
            }
        }
    }

    @ReadingConverter
    public record LinkedImageReadingConverter(ObjectMapper objectMapper)
            implements Converter<PGobject, LinkedImageRegions> {

        @Override
        public LinkedImageRegions convert(PGobject pgObject) {
            try {
                if (pgObject == null || pgObject.getValue() == null) {
                    return new LinkedImageRegions();
                }
                return objectMapper.readValue(pgObject.getValue(), LinkedImageRegions.class);
            } catch (Exception e) {
                throw new RuntimeException("Failed to convert JSONB to LinkedImageRegions", e);
            }
        }
    }
}
