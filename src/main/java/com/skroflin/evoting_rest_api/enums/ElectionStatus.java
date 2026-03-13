package com.skroflin.evoting_rest_api.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum ElectionStatus {

    PREPARATION("Preparation", 1),
    ACTIVE("Active", 2),
    CLOSED("Closed", 3),
    UNKNOWN("Unknown", 4);

    private final String name;
    private final int value;

    ElectionStatus(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    @JsonCreator
    public static ElectionStatus fromValue(Integer value) {
        if (value == null) return null;
        return Arrays.stream(values())
                .filter(r -> r.value == value)
                .findFirst()
                .orElse(UNKNOWN);
    }

    @JsonValue
    public String toJson() {
        return name;
    }

    public static ElectionStatus get(int value) {
        return fromValue(value);
    }
}
