package dev.sezrr.projects.patikaweatherproject.core.exception;

import dev.sezrr.projects.patikaweatherproject.core.model.customresponseentity.ErrorResponseEntity;
import dev.sezrr.projects.patikaweatherproject.core.model.customresponseentity.error.Error;
import dev.sezrr.projects.patikaweatherproject.core.model.customresponseentity.error.ErrorDeclaration;
import dev.sezrr.projects.patikaweatherproject.core.model.customresponseentity.error.ErrorStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class IllegalArgumentExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponseEntity<Error> handleIllegalArgumentException(IllegalArgumentException ex)
    {
        return ErrorResponseEntity.error(
                new Error(
                        new ErrorDeclaration(ex.getMessage()),
                        new ErrorStatus("Invalid Argument", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.name())
                ),
                HttpStatus.BAD_REQUEST
        );
    }
}
