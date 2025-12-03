package com.example.hangeulhunters.domain.conversation.converter;

import com.example.hangeulhunters.domain.conversation.vo.ImprovementItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * ImprovementItem 리스트를 JSON 문자열로 변환하는 JPA Converter
 */
@Slf4j
@Converter
public class ImprovementListConverter implements AttributeConverter<List<ImprovementItem>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<ImprovementItem> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "[]";
        }

        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert ImprovementItem list to JSON", e);
            return "[]";
        }
    }

    @Override
    public List<ImprovementItem> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty() || dbData.equals("[]")) {
            return new ArrayList<>();
        }

        try {
            return objectMapper.readValue(dbData, new TypeReference<List<ImprovementItem>>() {
            });
        } catch (JsonProcessingException e) {
            log.error("Failed to convert JSON to ImprovementItem list: {}", dbData, e);
            return new ArrayList<>();
        }
    }
}
