package dev.sezrr.projects.patikaweatherproject.core.model.customresponseentity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomResponseEntity<T>
{
    private boolean success;
    private String message;
    private T data;

    public CustomResponseEntity(boolean success)
    {
        this.success = success;
    }

    public CustomResponseEntity(String message, boolean success)
    {
        this(success);
        this.message = message;
    }

    public CustomResponseEntity(boolean success, T data)
    {
        this(success);
        this.data = data;
    }

    public CustomResponseEntity(String message, boolean success, T data)
    {
        this(message, success);
        this.data = data;
    }

    public CustomResponseEntity(T data)
    {
        this.data = data;
    }
}