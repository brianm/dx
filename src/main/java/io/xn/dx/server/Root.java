package io.xn.dx.server;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class Root
{
    @Inject
    public Root() {

    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String index()
    {
        return "hello world";
    }

//    @GET
//    @Path("suspend")
//    @Produces("text/plain")
//    public void async(final @Suspended AsyncResponse response,
//                      final @NotNull(message = "time query parameter required") @QueryParam("time") Long time)
//    {
//        Thread t = new Thread()
//        {
//            @Override
//            public void run()
//            {
//                try {
//                    Thread.sleep(time);
//                    response.resume(Response.ok("basic").type(MediaType.TEXT_PLAIN).build());
//                }
//                catch (Exception e) {
//                    log.warn("exception resuming!", e);
//                    response.resume(Response.serverError().type(MediaType.TEXT_PLAIN).entity(e.getMessage()).build());
//                }
//            }
//        };
//        t.start();
//    }
}
