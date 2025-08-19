package dev.sezrr.projects.patikaweatherproject.core.validation;

public class ValidationConstant {
    private ValidationConstant() {
        // Prevent instantiation
        throw new IllegalStateException("This is a utility class and cannot be instantiated");
    }

    public static final String VALIDATION_FAILED = "Validation failed due to incorrect input values.";
}