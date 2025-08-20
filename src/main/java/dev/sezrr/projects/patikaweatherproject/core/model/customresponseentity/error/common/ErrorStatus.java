package dev.sezrr.projects.patikaweatherproject.core.model.customresponseentity.error.common;

import lombok.Getter;

@Getter
public class ErrorStatus
{
    private String error;
    private final int errorCode;
    private String errorCategory;

    public ErrorStatus(String error, int errorCode, String errorCategory)
    {
        this.error = error;
        this.errorCode = errorCode;
        this.errorCategory = errorCategory;
    }

    public ErrorStatus(String error, int errorCode)
    {
        this.error = error;
        this.errorCode = errorCode;
    }

    public ErrorStatus(int errorCode, String errorCategory)
    {
        this.errorCode = errorCode;
        this.errorCategory = errorCategory;
    }
}