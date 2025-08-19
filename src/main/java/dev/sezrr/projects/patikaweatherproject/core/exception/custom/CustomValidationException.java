package dev.sezrr.projects.patikaweatherproject.core.exception.custom;

import dev.sezrr.projects.patikaweatherproject.core.model.customresponseentity.error.common.ErrorDeclaration;
import dev.sezrr.projects.patikaweatherproject.core.model.customresponseentity.error.common.ErrorStatus;
import dev.sezrr.projects.patikaweatherproject.core.model.customresponseentity.error.custom.ValidationError;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CustomValidationException extends RuntimeException {
    private final BindingResult bindingResult;

    public CustomValidationException(BindingResult bindingResult, String message) {
        super(message);
        this.bindingResult = bindingResult;
    }

    public static ResponseEntity<ValidationError> handleValidationException(CustomValidationException ex) {
        // Collect validation errors
        List<ErrorDeclaration> errorDeclarations = new ArrayList<>();
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            errorDeclarations.add(new ErrorDeclaration(error.getDefaultMessage()));
        }

        // Create custom ValidationError response
        ValidationError response = new ValidationError(
                errorDeclarations,
                new ErrorStatus("VALIDATION_ERROR", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.name()),
                ex.getMessage()
        );

        // Return ResponseEntity with custom response object
        return ResponseEntity.badRequest().body(response);
    }
}