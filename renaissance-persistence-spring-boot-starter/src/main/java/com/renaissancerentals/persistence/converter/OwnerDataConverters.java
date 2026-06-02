package com.renaissancerentals.persistence.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renaissancerentals.persistence.entity.SplitBillEntity;
import org.postgresql.util.PGobject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

public class OwnerDataConverters {

    @WritingConverter
    public record OwnerDataWritingConverter(ObjectMapper objectMapper)
            implements Converter<SplitBillEntity.OwnerData, PGobject> {
        @Override
        public PGobject convert(SplitBillEntity.OwnerData source) {
            try {
                PGobject jsonObject = new PGobject();
                jsonObject.setType("jsonb");
                jsonObject.setValue(objectMapper.writeValueAsString(source));
                return jsonObject;
            } catch (Exception e) {
                throw new RuntimeException("Failed to serialize SettingsData to JSON", e);
            }
        }
    }

    @ReadingConverter
    public record OwnerDataReadingConverter(ObjectMapper objectMapper)
            implements Converter<PGobject, SplitBillEntity.OwnerData> {
        @Override
        public SplitBillEntity.OwnerData convert(PGobject source) {
            try {
                return objectMapper.readValue(source.getValue(), SplitBillEntity.OwnerData.class);
            } catch (Exception e) {
                throw new RuntimeException("Failed to deserialize JSON to OwnerData", e);
            }
        }
    }
}
