package io.xn.dx.ext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

public class JacksonEntity extends StringEntity
{

    private static final ObjectMapper mapper = new ObjectMapper();

    public JacksonEntity(final Object entity) throws JsonProcessingException
    {
        super(mapper.writeValueAsString(entity), ContentType.APPLICATION_JSON);
    }
}
