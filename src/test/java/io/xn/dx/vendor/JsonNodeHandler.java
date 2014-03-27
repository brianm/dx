package io.xn.dx.vendor;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;

import java.io.IOException;

public class JsonNodeHandler implements ResponseHandler<JsonNode>
{
    @Override
    public JsonNode handleResponse(final HttpResponse response) throws IOException
    {
        int status = response.getStatusLine().getStatusCode();
        if (status >= 300 || status < 200) {
            throw new HttpResponseException(response.getStatusLine().getStatusCode(),
                                            response.getStatusLine().getReasonPhrase());

        }
        return Jackson.getReader().readTree(response.getEntity().getContent());
    }
}
