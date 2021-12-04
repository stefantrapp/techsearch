package de.fernunihagen.techsearch.restservice.dtos;

import java.io.IOException;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class UploadDataDtoConverter implements Converter<String, UploadDataDto> {

    private final ObjectMapper objectMapper;

    public UploadDataDtoConverter (ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public UploadDataDto convert(String source) {
        try {
            return objectMapper.readValue(source, UploadDataDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}