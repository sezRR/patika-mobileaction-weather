package dev.sezrr.projects.patikaweatherproject.core.exception;

import dev.sezrr.projects.patikaweatherproject.core.model.customresponseentity.ErrorResponseEntity;
import dev.sezrr.projects.patikaweatherproject.core.model.customresponseentity.error.Error;
import dev.sezrr.projects.patikaweatherproject.core.model.customresponseentity.error.ErrorDeclaration;
import dev.sezrr.projects.patikaweatherproject.core.model.customresponseentity.error.ErrorStatus;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class EntityNotFoundExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResponseEntity<Error> handleEntityNotFoundException(EntityNotFoundException ex) {
        return ErrorResponseEntity.error(
                new Error(
                        new ErrorDeclaration(ex.getMessage()),
                        new ErrorStatus("City Not Found", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.name())
                ),
                HttpStatus.NOT_FOUND
        );
    }
}
