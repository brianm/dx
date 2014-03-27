package io.xn.dx.vendor;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;

import java.io.IOException;

public class JsonNodeHandler implements ResponseHandler<JsonNode>
{
    @Override
    public JsonNode handleResponse(final HttpResponse response) throws IOException
    {
        return Jackson.getReader().readTree(response.getEntity().getContent());
    }
}
