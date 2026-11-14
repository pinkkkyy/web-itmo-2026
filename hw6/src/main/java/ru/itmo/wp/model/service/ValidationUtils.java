package ru.itmo.wp.model.service;

import com.google.common.base.Strings;
import ru.itmo.wp.model.exception.ValidationException;

final class ValidationUtils {
    private ValidationUtils() {
    }

    static long parseId(String value, String entityName) throws ValidationException {
        if (Strings.isNullOrEmpty(value)) {
            throw new ValidationException(entityName + " is required");
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid " + entityName + " id");
        }
    }

    static boolean parseFlag(String value, String name) throws ValidationException {
        if ("true".equals(value)) {
            return true;
        }
        if ("false".equals(value)) {
            return false;
        }
        throw new ValidationException("Invalid value of " + name);
    }

    static String validateText(String value, String name, int maxLength) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(name + " is required");
        }
        String trimmed = value.trim();
        if (trimmed.length() > maxLength) {
            throw new ValidationException(name + " can't be longer than " + maxLength + " characters");
        }
        return trimmed;
    }
}
