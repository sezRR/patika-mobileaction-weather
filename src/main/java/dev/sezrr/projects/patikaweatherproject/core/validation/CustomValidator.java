package dev.sezrr.projects.patikaweatherproject.core.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.Set;

@Component
public class CustomValidator
{
    private final Validator validator;

    @Autowired
    public CustomValidator(Validator validator)
    {
        this.validator = validator;
    }

    public BindingResult validate(Object validationObject)
    {
        Set<ConstraintViolation<Object>> violations = validator.validate(validationObject);

        BindingResult bindingResult = new BeanPropertyBindingResult(validationObject, "object");
        for (var violation : violations)
        {
            ObjectError objectError = new ObjectError(validationObject.toString(), violation.getMessage());
            bindingResult.addError(objectError);
        }

        return bindingResult;
    }
}