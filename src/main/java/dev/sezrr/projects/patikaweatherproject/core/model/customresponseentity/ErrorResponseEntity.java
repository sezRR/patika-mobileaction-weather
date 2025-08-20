package dev.sezrr.projects.patikaweatherproject.core.model.customresponseentity;

import dev.sezrr.projects.patikaweatherproject.core.model.customresponseentity.error.common.Error;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

public class ErrorResponseEntity<T extends Error> extends ResponseEntity<T>
{
    public ErrorResponseEntity(T error, HttpStatusCode status)
    {
        super(error, status);
    }

    public static <T extends Error> ErrorResponseEntity<T> error(T error, HttpStatusCode status)
    {
        return new ErrorResponseEntity<>(error, status);
    }
}
