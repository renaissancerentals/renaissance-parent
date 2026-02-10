package com.renaissancerentals.persistence.converter;

import org.postgresql.util.PGobject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.lang.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renaissancerentals.persistence.entity.ContactAdditionalInfo;

@ReadingConverter
public class AdditionalInfoReadingConverter implements Converter<PGobject, ContactAdditionalInfo> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override

    public ContactAdditionalInfo convert(@NonNull PGobject source){
        try {
            return objectMapper.readValue(source.getValue(),ContactAdditionalInfo.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to convert JSONB to AdditionalInfo", e);
        }
    }
}
