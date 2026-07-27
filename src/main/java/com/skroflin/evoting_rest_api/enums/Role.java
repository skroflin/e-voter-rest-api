package com.skroflin.evoting_rest_api.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum Role {
    ROLE_SUPER_ADMIN("Super admin", 0),
    ROLE_ELECTION_ADMIN("Admin", 1),
    ROLE_VOTER("Voter", 2),
    UNKNOWN("Unknown", -1);

    private final String name;
    private final int value;

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
