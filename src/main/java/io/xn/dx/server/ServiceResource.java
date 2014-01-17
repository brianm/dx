package io.xn.dx.server;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import io.xn.dx.Descriptor;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

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
    public Response post(@Context UriInfo uris, Descriptor d)
    {

        Descriptor stored = storage.create(d);
        Preconditions.checkState(stored.getInstanceId().isPresent(), "_id must be present on descriptor after storing it");

        URI uri = uris.getRequestUriBuilder().path(ServiceResource.class, "getById").build(stored.getInstanceId().get());

        return Response.created(uri).entity(stored.withSelf(uri)).build();
    }

    @GET
    @Path("/{id}")
    @Produces("application/json")
    public Descriptor getById(@Context UriInfo uris, @PathParam("id") String id)
    {
        Optional<Descriptor> d = storage.lookup(id);
        if (d.isPresent()) {
            return d.get().withSelf(uris.getRequestUri());
        }
        else {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }
}
