package com.kaz.townsq.model;

import com.kaz.townsq.exception.InvalidRoleException;

import java.util.Arrays;

public enum Role {
    ADMIN,
    ACCOUNT_MANAGER,
    DEFAULT;

    public static Role fromString(String value) {
        try {
            return Role.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidRoleException(
                    "Invalid role: " + value + ". Valid roles are: " +
                            Arrays.toString(Role.values())
            );
        }
    }
}