package com.liberty.book.crawler.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import java.util.Map;

/**
 * User: Dimitr
 * Date: 09.06.2017
 * Time: 8:50
 */
public class MapConverter implements AttributeConverter<Map, String> {
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map map) {
        try {
            return mapper.writeValueAsString(map);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    @Override
    public Map<Integer, String> convertToEntityAttribute(String json) {
        try {
            return (Map<Integer, String>) mapper.readValue(json, new TypeReference<Map<Integer, String>>() {
            });
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }
}
