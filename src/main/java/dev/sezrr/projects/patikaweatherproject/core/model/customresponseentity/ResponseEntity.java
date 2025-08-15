package dev.sezrr.projects.patikaweatherproject.core.model.customresponseentity;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.util.MultiValueMap;

@Getter
public class ResponseEntity<T> extends org.springframework.http.ResponseEntity<CustomResponseEntity<T>> {
    public ResponseEntity(HttpStatusCode status) {
        super(status);
    }

    public ResponseEntity(T body, HttpStatusCode status, boolean success) {
        super(new CustomResponseEntity<>(success, body), status);
    }

    public ResponseEntity(MultiValueMap<String, String> headers, HttpStatusCode status) {
        super(headers, status);
    }

    public ResponseEntity(T body, MultiValueMap<String, String> headers, int rawStatus) {
        super(new CustomResponseEntity<>(body), headers, rawStatus);
    }

    public ResponseEntity(T body, MultiValueMap<String, String> headers, HttpStatusCode statusCode) {
        super(new CustomResponseEntity<>(body), headers, statusCode);
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
