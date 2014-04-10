package io.xn.dx.server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;
import io.airlift.units.Duration;
import io.xn.dx.storage.Storage;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

public class HeartbeatResource
{
    private final Storage storage;
    private final String id;

    public HeartbeatResource(final Storage storage, final String id)
    {
        this.storage = storage;
        this.id = id;
    }

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response post(TtlMessage ttl)
    {
        Optional<Duration> extension = storage.heartbeat(id, ttl.getTtl());
        if (extension.isPresent()) {
            return Response.ok().entity(new TtlMessage(extension.get())).build();
        }
        else
        {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    public static class TtlMessage
    {
        private final Duration ttl;

        @JsonCreator
        public TtlMessage(@JsonProperty("ttl") Duration ttl)
        {
            this.ttl = ttl;
        }

        public Duration getTtl()
        {
            return ttl;
        }
    }
}
