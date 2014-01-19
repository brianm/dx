package io.xn.dx.jaxrs;

import com.google.common.collect.Lists;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ResteasyViolationException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.List;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ResteasyViolationException>
{
    @Override
    public Response toResponse(final ResteasyViolationException e)
    {
        List<String> messages = Lists.newArrayList();
        for (ResteasyConstraintViolation violation : e.getViolations()) {
            messages.add(violation.getMessage());
        }
        return Response.status(400)
                       .type(MediaType.APPLICATION_JSON_TYPE)
                       .entity(messages)
                       .build();
    }
}