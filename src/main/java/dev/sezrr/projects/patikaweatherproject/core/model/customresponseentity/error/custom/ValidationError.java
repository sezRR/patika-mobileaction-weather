package dev.sezrr.projects.patikaweatherproject.core.model.customresponseentity.error.custom;

import dev.sezrr.projects.patikaweatherproject.core.model.customresponseentity.error.common.Error;
import dev.sezrr.projects.patikaweatherproject.core.model.customresponseentity.error.common.ErrorDeclaration;
import dev.sezrr.projects.patikaweatherproject.core.model.customresponseentity.error.common.ErrorStatus;

import java.util.List;

public class ValidationError extends Error
{
    public ValidationError(List<ErrorDeclaration> errorDeclarations, ErrorStatus errorStatus, String systemMessage) {
        super(errorDeclarations, errorStatus, systemMessage);
    }
}