package org.javarush.entity;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Feature {
    TRAILERS("Trailers"),
    COMMENTARIES("Commentaries"),
    DELETED_SCENES("Deleted Scenes"),
    BEHIND_THE_SCENES("Behind the Scenes");

    private final String value;

    Feature(String value) {
        this.value = value;
    }

    public static Feature getFeatureByValue(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        return Arrays.stream(Feature.values())
                .filter(feature -> feature.getValue().equals(value))
                .findFirst()
                .orElse(null);
    }

}