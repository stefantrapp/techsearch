package de.fernunihagen.techsearch.restservice.dtos.mapping;

import java.util.Arrays;
import java.util.List;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;

import de.fernunihagen.techsearch.data.CrawlJob;
import de.fernunihagen.techsearch.data.Document;
import de.fernunihagen.techsearch.restservice.dtos.CrawlJobDto;
import de.fernunihagen.techsearch.restservice.dtos.DocumentBasicDto;
import de.fernunihagen.techsearch.restservice.dtos.DocumentFullDto;
import de.fernunihagen.techsearch.restservice.dtos.PageableDto;

/**
 * Konfiguration für den ModelMapper für die einfache Befüllung von DTOs.
 */

@Configuration
public class ModelMapperConfiguration {
    
    @Bean
    public ModelMapper createModelMapper(){
    
        ModelMapper modelMapper = new ModelMapper();
        
        Converter<List<String>, String> listToMultiLineString = new Converter<List<String>, String>() {
            public String convert(MappingContext<List<String>, String> context) {
                var list = context.getSource();
                String value;
                if (list != null && list.size() > 0) {
                    value = String.join("\n", list);
                } else {
                    value = "";
                }

                return value;
            }
        };
        
        Converter<String, List<String>> multiLineStringToList = new Converter<String, List<String>>() {
            public List<String> convert(MappingContext<String, List<String>> context) {
                var string = context.getSource();
                List<String> value;
                if (string != null) {
                    value = Arrays.asList(string.split("\n"));
                } else {
                    value = null;
                }

                return value;
            }
        };
        
        Converter<String, String> limitText = new Converter<String, String>() {
            public String convert(MappingContext<String, String> context) {
                var text = context.getSource();
                String value;
                if (text != null) {
                    value = text.substring(0, Math.min(100, text.length()));
                } else {
                    value = "";
                }

                return value;
            }
        };
        
        Converter<String, Long> getStringLength = new Converter<String, Long>() {
            public Long convert(MappingContext<String, Long> context) {
                var text = context.getSource();
                long value;
                if (text != null) {
                    value = text.length();
                } else {
                    value = 0;
                }

                return value;
            }
        };
        
        Converter<byte[], Long> getByteArrayLength = new Converter<byte[], Long>() {
            public Long convert(MappingContext<byte[], Long> context) {
                var text = context.getSource();
                long value;
                if (text != null) {
                    value = text.length;
                } else {
                    value = 0;
                }

                return value;
            }
        };

        modelMapper.addMappings(new PropertyMap<CrawlJob, CrawlJobDto>() {
            protected void configure() {
                using(listToMultiLineString).map(source.getUrls()).setUrls(null);
            }
        });
        
        modelMapper.addMappings(new PropertyMap<CrawlJobDto, CrawlJob>() {
            protected void configure() {
                using(multiLineStringToList).map(source.getUrls()).setUrls(null);
            }
        });
        
        modelMapper.createTypeMap(Page.class, PageableDto.class)
            .addMappings(mapper -> mapper.map(Page::getTotalElements, PageableDto::setTotalElements))
            .addMappings(mapper -> mapper.map(Page::getTotalPages, PageableDto::setTotalPages));
        
        modelMapper.createTypeMap(Document.class, DocumentBasicDto.class)
        .addMappings(mapper -> mapper.map(Document::getName, DocumentBasicDto::setName))
        .addMappings(new PropertyMap<Document, DocumentBasicDto>() {
            protected void configure() {
                using(limitText).map(source.getSentences()).setText(null);
                using(getByteArrayLength).map(source.getOriginalContent()).setOriginalBinaryLength(0);
                using(getStringLength).map(source.getOriginalTextContent()).setOriginalTextLength(0);
                using(getStringLength).map(source.getSentences()).setSentenceLength(0);
            }
        });
        
        modelMapper.createTypeMap(Document.class, DocumentFullDto.class)
        .addMappings(mapper -> mapper.map(Document::getName, DocumentBasicDto::setName))
        .addMappings(new PropertyMap<Document, DocumentFullDto>() {
            protected void configure() {
                using(limitText).map(source.getSentences()).setText(null);
                using(getByteArrayLength).map(source.getOriginalContent()).setOriginalBinaryLength(0);
                using(getStringLength).map(source.getOriginalTextContent()).setOriginalTextLength(0);
                using(getStringLength).map(source.getSentences()).setSentenceLength(0);
            }
        });
        
        
        
        return modelMapper;
    }
}
