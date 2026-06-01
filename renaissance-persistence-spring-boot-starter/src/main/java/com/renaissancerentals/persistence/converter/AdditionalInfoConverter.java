package com.renaissancerentals.persistence.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renaissancerentals.persistence.entity.ContactAdditionalInfo;
import org.postgresql.util.PGobject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

public class AdditionalInfoConverter {
    @WritingConverter
    public record AdditionalInfoWritingConverter(ObjectMapper objectMapper)
            implements Converter<ContactAdditionalInfo, PGobject> {

        @Override
        public PGobject convert(ContactAdditionalInfo source) {
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

    @ReadingConverter
    public record AdditionalInfoReadingConverter(ObjectMapper objectMapper)
            implements Converter<PGobject, ContactAdditionalInfo> {

        @Override
        public ContactAdditionalInfo convert(PGobject source) {
            try {
                return objectMapper.readValue(source.getValue(), ContactAdditionalInfo.class);
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to convert JSONB to AdditionalInfo", e);
            }
        }
    }
}
