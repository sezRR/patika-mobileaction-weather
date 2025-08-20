package dev.sezrr.projects.patikaweatherproject.core.exception.handler;

import dev.sezrr.projects.patikaweatherproject.core.model.customresponseentity.ErrorResponseEntity;
import dev.sezrr.projects.patikaweatherproject.core.model.customresponseentity.error.common.ErrorDeclaration;
import dev.sezrr.projects.patikaweatherproject.core.model.customresponseentity.error.common.ErrorStatus;
import dev.sezrr.projects.patikaweatherproject.core.model.customresponseentity.error.common.Error;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@ControllerAdvice
public class WebClientResponseExceptionHandler
{
    @ExceptionHandler(WebClientResponseException.class)
    public ErrorResponseEntity<Error> handleWebClientResponseException(WebClientResponseException ex)
    {
        return ErrorResponseEntity.error(
                new Error(
                        new ErrorDeclaration("Something went wrong while communicating with an external service. " + ex.getMessage()),
                        new ErrorStatus("WebClient Error", HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.name())
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
