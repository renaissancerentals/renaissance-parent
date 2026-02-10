package com.renaissancerentals.persistence.converter;

import org.postgresql.util.PGobject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renaissancerentals.persistence.entity.ContactAdditionalInfo;

@WritingConverter
public class AdditionalInfoWritingConverter implements Converter<ContactAdditionalInfo, PGobject> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public PGobject convert(@NonNull ContactAdditionalInfo source){

        try {
            PGobject pgObject = new PGobject();
            pgObject.setType("jsonb");
            pgObject.setValue(objectMapper.writeValueAsString(source));
            return pgObject;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to convert AdditionalInfo to JSONB", e);
        }
    }
}
