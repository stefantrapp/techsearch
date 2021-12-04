package de.fernunihagen.techsearch.data.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import de.fernunihagen.techsearch.data.ApplicationJobEnum;

/**
 * Konverter für die Klasse ApplicationJobEnum zur Speicherung der Werte in der Datenbank.
 * Damit werden die Namen des Jobs gespeichert und nicht das Ordinal aus dem Enum.
 * In der Datenbank sind sie daruch einfacher für einen Menschen lesbar.
 */

@Converter
public class ApplicationJobConverter implements AttributeConverter<ApplicationJobEnum, String> {

    @Override
    public String convertToDatabaseColumn(ApplicationJobEnum attribute) {
        return attribute.getName();
    }

    @Override
    public ApplicationJobEnum convertToEntityAttribute(String dbData) {
        return ApplicationJobEnum.fromName(dbData);
    }

}
