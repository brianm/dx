package io.xn.dx.server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;
import io.xn.dx.reps.Service;
import io.xn.dx.reps.Status;
import io.xn.dx.storage.Storage;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

public class StatusResource
{
    private final Storage storage;
    private final String id;

    public StatusResource(Storage storage, String serviceId)
    {
        this.storage = storage;
        id = serviceId;
    }

    @GET
    @Produces("application/json")
    public Response get()
    {
        Optional<Service> s = storage.lookup(id);
        if (s.isPresent()) {
            return Response.ok(new StatusMessage(s.get().getStatus())).build();
        }
        else
        {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response post(StatusMessage msg)
    {
        Optional<Service> s = storage.updateStatus(id, msg.getStatus());
        if (s.isPresent()) {
            return Response.ok(s.get()).build();
        }
        else
        {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    public static class StatusMessage
    {
        private final Status status;

        @JsonCreator
        StatusMessage(@JsonProperty("status") Status status)
        {
            this.status = status;
        }

        public Status getStatus()
        {
            return status;
        }
    }
}
