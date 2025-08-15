package dev.sezrr.projects.patikaweatherproject.core.model.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.HashMap;
import java.util.Map;

@Converter
public class MapToJsonConverter implements AttributeConverter<Map<String, Object>, String>
{
    private static final ObjectMapper M = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> m)
    {
        try
        {
            return M.writeValueAsString(m == null ? Map.of() : m);
        } catch (Exception e)
        {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String json)
    {
        try
        {
            return json == null ? new HashMap<>() : M.readValue(json, new TypeReference<>()
            { });
        } catch (Exception e)
        {
            throw new IllegalArgumentException(e);
        }
    }
}
