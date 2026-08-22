package com.skroflin.evoting_rest_api.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum ElectionStatus {

    PREPARATION("Preparation", 0),
    ACTIVE("Active", 1),
    CLOSED("Closed", 2),
    UNKNOWN("Unknown", -1);

    private final String name;
    private final int value;

    @JsonCreator
    public static ElectionStatus fromValue(Object value) {
        if (value instanceof Integer integerVal) {
            for (ElectionStatus status : values()) {
                if (status.value == integerVal || status.ordinal() == integerVal) {
                    return status;
                }
            }
        } else if (value instanceof String stringVal) {
            for (ElectionStatus status : values()) {
                if (status.name().equalsIgnoreCase(stringVal)) {
                    return status;
                }
            }
        }
        throw new IllegalArgumentException("Unknown status: " + value);
    }

    @JsonValue
    public String toJson() {
        return name;
    }

    public static ElectionStatus get(int value) {
        return fromValue(value);
    }
}
