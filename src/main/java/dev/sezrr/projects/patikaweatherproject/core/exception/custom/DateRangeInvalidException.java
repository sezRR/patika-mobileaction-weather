package dev.sezrr.projects.patikaweatherproject.core.exception.custom;

import org.springframework.validation.BindingResult;

public class DateRangeInvalidException extends CustomValidationException
{
    public DateRangeInvalidException(BindingResult bindingResult, String message)
    {
        super(bindingResult, message);
    }

    public DateRangeInvalidException()
    {
        super(null, "");
    }
}
