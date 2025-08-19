package dev.sezrr.projects.patikaweatherproject.core.validation;

import dev.sezrr.projects.patikaweatherproject.core.exception.custom.CustomValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomValidatorHelper {
    private final CustomValidator beanValidation;

    @Autowired
    public CustomValidatorHelper(CustomValidator beanValidation) {
        this.beanValidation = beanValidation;
    }

    public <T> ValidationResponse validate(T object){
        var bindingResult = beanValidation.validate(object);

        if (bindingResult.hasErrors())
            return new ValidationResponse(false, bindingResult.getAllErrors());

        return new ValidationResponse(true);
    }

    public <T> void validateOrThrow(T object, String message){
        var bindingResult = beanValidation.validate(object);

        if (bindingResult.hasErrors())
            throw new CustomValidationException(bindingResult, message);
    }
}