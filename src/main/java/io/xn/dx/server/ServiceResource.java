package io.xn.dx.server;

import com.google.common.base.Optional;
import io.xn.dx.reps.Service;
import io.xn.dx.reps.ServiceSet;
import io.xn.dx.storage.Storage;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;

@Path("/srv")
@Singleton
public class ServiceResource
{
    private final Storage storage;

    @Inject
    public ServiceResource(Storage storage)
    {
        this.storage = storage;
    }

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response post(Service service)
    {
        final Service stored = storage.create(service);
        return Response.created(stored.getLinks().get("self").getHref()).entity(stored).build();
    }

    @GET
    @Produces("application/json")
    public ServiceSet query()
    {
        Set<Service> services = storage.query();
//        return ServiceSet.(services);
        throw new UnsupportedOperationException("Not Yet Implemented!");
    }

    @GET
    @Path("/{id}")
    @Produces("application/json")
    public Service getById(@PathParam("id") String id)
    {
        Optional<Service> d = storage.lookup(id);
        if (d.isPresent()) {
            return d.get();
        }
        else {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }
}

