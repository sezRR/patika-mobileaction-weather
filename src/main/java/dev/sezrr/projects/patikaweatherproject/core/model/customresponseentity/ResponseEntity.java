package dev.sezrr.projects.patikaweatherproject.core.model.customresponseentity;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Getter
public class ResponseEntity<T> extends org.springframework.http.ResponseEntity<CustomResponseEntity<T>> {
    public ResponseEntity(T body, HttpStatusCode status, boolean success) {
        super(new CustomResponseEntity<>(success, body), status);
    }

    public static <T> ResponseEntity<T> success(T data)
    {
        return new ResponseEntity<>(data, HttpStatus.OK, true);
    }

    public static <T> ResponseEntity<T> success(T data, HttpStatus status)
    {
        return new ResponseEntity<>(data, status, true);
    }

    public static <T> ResponseEntity<T> fail(T data)
    {
        return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST, false);
    }

    public static <T> ResponseEntity<T> fail(T data, HttpStatus status)
    {
        return new ResponseEntity<>(data, status, false);
    }
}
