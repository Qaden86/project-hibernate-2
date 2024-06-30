package org.javarush.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.javarush.entity.Rating;

@Converter(autoApply = true)
public class RatingConverter implements AttributeConverter<Rating, String> {
    @Override
    public String convertToDatabaseColumn(Rating attribute) {
        return attribute.getValue();
    }

    @Override
    public Rating convertToEntityAttribute(String databaseData) {
        Rating[] ratings = Rating.values();
        for (Rating rating : ratings) {
            if (rating.getValue().equals(databaseData)) {
                return rating;
            }
        }
        return null;
    }

}
