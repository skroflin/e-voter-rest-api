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
