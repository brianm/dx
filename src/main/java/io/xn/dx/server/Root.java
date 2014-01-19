package io.xn.dx.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class Root
{
    private final String message;
    private final Logger log = LoggerFactory.getLogger(Root.class);


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

    @GET
    @Path("suspend")
    @Produces("text/plain")
    public void async(final @Suspended AsyncResponse response,
                      final @NotNull(message = "time query parameter required") @QueryParam("time") Long time)
    {
        Thread t = new Thread()
        {
            @Override
            public void run()
            {
                try {
                    Thread.sleep(time);
                    response.resume(Response.ok("basic").type(MediaType.TEXT_PLAIN).build());
                }
                catch (Exception e) {
                    log.warn("exception resuming!", e);
                    response.resume(Response.serverError().type(MediaType.TEXT_PLAIN).entity(e.getMessage()).build());
                }
            }
        };
        t.start();
    }
}
