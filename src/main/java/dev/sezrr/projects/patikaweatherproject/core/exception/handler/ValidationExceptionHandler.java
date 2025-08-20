package dev.sezrr.projects.patikaweatherproject.core.exception.handler;

import dev.sezrr.projects.patikaweatherproject.core.exception.custom.CustomValidationException;
import dev.sezrr.projects.patikaweatherproject.core.exception.custom.DateRangeInvalidException;
import dev.sezrr.projects.patikaweatherproject.core.model.customresponseentity.error.common.ErrorStatus;
import dev.sezrr.projects.patikaweatherproject.core.model.customresponseentity.error.custom.ValidationError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ValidationExceptionHandler {
    @ExceptionHandler(DateRangeInvalidException.class)
    public ResponseEntity<ValidationError> handleDateRangeInvalidException(DateRangeInvalidException ex)
    {
        return CustomValidationException.handleCustomValidationException(ex,
                new ErrorStatus("DATE_RANGE_INVALID_ERROR", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.name())
        );
    }

    @ExceptionHandler(CustomValidationException.class)
    public ResponseEntity<ValidationError> handleCustomValidationException(CustomValidationException ex)
    {
        return CustomValidationException.handleValidationException(ex);
    }
}
