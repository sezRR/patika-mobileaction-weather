package dev.sezrr.projects.patikaweatherproject.core.model.customresponseentity.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Error {
    private final List<ErrorDeclaration> errorDeclarations;
    private final ErrorStatus errorStatus;
    private final String systemMessage;

    public Error(List<ErrorDeclaration> errorDeclarations, ErrorStatus errorStatus, String systemMessage) {
        this.errorDeclarations = errorDeclarations;
        this.errorStatus = errorStatus;
        this.systemMessage = systemMessage;
    }

    public Error(List<ErrorDeclaration> errorDeclarations, ErrorStatus errorStatus) {
        this(errorDeclarations, errorStatus, null);
    }

    public Error(ErrorDeclaration errorDeclaration, ErrorStatus errorStatus, String systemMessage) {
        this(Collections.singletonList(errorDeclaration), errorStatus, systemMessage);
    }

    public Error(ErrorDeclaration errorDeclaration, ErrorStatus errorStatus) {
        this(Collections.singletonList(errorDeclaration), errorStatus, null);
    }
}