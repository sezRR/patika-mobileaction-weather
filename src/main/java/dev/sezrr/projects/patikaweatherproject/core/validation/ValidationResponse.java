package dev.sezrr.projects.patikaweatherproject.core.validation;

import lombok.Getter;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ValidationResponse
{
    private final boolean isValid;
    private final List<ObjectError> errors;

    public ValidationResponse(boolean isValid)
    {
        this.isValid = isValid;
        this.errors = new ArrayList<>();
    }

    public ValidationResponse(boolean isValid, List<ObjectError> errors)
    {
        this.isValid = isValid;
        this.errors = errors;
    }
}