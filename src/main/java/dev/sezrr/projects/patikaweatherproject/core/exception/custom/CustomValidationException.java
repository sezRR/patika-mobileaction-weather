package dev.sezrr.projects.patikaweatherproject.core.exception.custom;

import dev.sezrr.projects.patikaweatherproject.core.model.customresponseentity.error.common.ErrorDeclaration;
import dev.sezrr.projects.patikaweatherproject.core.model.customresponseentity.error.common.ErrorStatus;
import dev.sezrr.projects.patikaweatherproject.core.model.customresponseentity.error.custom.ValidationError;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CustomValidationException extends RuntimeException
{
    private BindingResult bindingResult;
    private String message;

    public CustomValidationException(BindingResult bindingResult, String message)
    {
        super(message);
        this.message = message;
        this.bindingResult = bindingResult;
    }

    private static List<ErrorDeclaration> createErrorDeclarations(BindingResult bindingResult)
    {
        // Collect validation errors
        List<ErrorDeclaration> errorDeclarations = new ArrayList<>();
        for (ObjectError error : bindingResult.getAllErrors())
        {
            errorDeclarations.add(new ErrorDeclaration(error.getDefaultMessage()));
        }
        return errorDeclarations;
    }

    public static ResponseEntity<ValidationError> handleValidationException(CustomValidationException ex)
    {
        // Create custom ValidationError response
        ValidationError response = new ValidationError(
                createErrorDeclarations(ex.getBindingResult()),
                new ErrorStatus("VALIDATION_ERROR", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.name()),
                ex.getMessage()
        );

        return ResponseEntity.badRequest().body(response);
    }

    public static <T extends CustomValidationException> ResponseEntity<ValidationError> handleCustomValidationException(T ex, ErrorStatus errorStatus)
    {
        // Create custom ValidationError response
        ValidationError response = new ValidationError(
                createErrorDeclarations(ex.getBindingResult()),
                errorStatus,
                ex.getMessage()
        );

        return ResponseEntity.badRequest().body(response);
    }
}