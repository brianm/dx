package io.xn.dx.server;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class Root
{
    private final String message;

    @Inject
    public Root(@Named("message") String message)
    {
        this.message = message;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String get()
    {
        return message;
    }
}
