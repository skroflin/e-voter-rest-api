package com.skroflin.evoting_rest_api.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum Role {
    ROLE_SUPER_ADMIN("Super admin", 1),
    ROLE_ELECTION_ADMIN("Active", 2),
    ROLE_VOTER("Voter", 3),
    UNKNOWN("Unknown", -1);

    private final String name;
    private final int value;

    Role(String name, int value) {
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
    public static Role fromValue(Integer value) {
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

    public static Role get(int value) {
        return fromValue(value);
    }
}
