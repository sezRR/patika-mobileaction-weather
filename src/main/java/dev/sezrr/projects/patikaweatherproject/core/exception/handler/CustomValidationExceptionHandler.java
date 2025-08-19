package dev.sezrr.projects.patikaweatherproject.core.exception.handler;

import dev.sezrr.projects.patikaweatherproject.core.exception.custom.CustomValidationException;
import dev.sezrr.projects.patikaweatherproject.core.model.customresponseentity.error.custom.ValidationError;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomValidationExceptionHandler {
    @ExceptionHandler(CustomValidationException.class)
    public ResponseEntity<ValidationError> handleCustomValidationException(CustomValidationException ex) {
        return CustomValidationException.handleValidationException(ex);
    }
}