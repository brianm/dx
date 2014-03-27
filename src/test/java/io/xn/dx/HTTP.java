package io.xn.dx;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.xn.dx.ext.JacksonEntity;
import io.xn.dx.vendor.JsonNodeHandler;
import org.apache.http.client.fluent.Request;

import java.io.IOException;
import java.net.URI;

public class HTTP
{
    public static JsonNode GET(URI uri) throws IOException
    {
        return Request.Get(uri).execute().handleResponse(new JsonNodeHandler());
    }

    public static JsonNode POST(URI uri, Object entity) throws IOException
    {
        return Request.Post(uri).body(new JacksonEntity(entity)).execute().handleResponse(new JsonNodeHandler());
    }
}
