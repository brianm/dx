package io.xn.dx.reps;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import io.xn.dx.version.Version;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.net.URI;
import java.util.Map;

public class Service
{
    /**
     * _links: {
     * self: { href: "/srv/123abc" },
     * status: { href: "/srv/123abc/status" },
     * },
     * status: "ok",
     * type: "memcached",
     * url: "memcached://10.0.1.100:11211",
     * pool: "general",
     * version: "0.2.3"
     */
    private final URI url;
    private final String pool;
    private final Version version;
    private final String type;
    private final Status status;
    private final Optional<String> id;
    private final Map<String, Link> links;

    @JsonCreator
    public Service(@JsonProperty("id") Optional<String> id,
                   @JsonProperty("url") URI url,
                   @JsonProperty("pool") String pool,
                   @JsonProperty("version") Version version,
                   @JsonProperty("type") String type,
                   @JsonProperty("status") Optional<Status> status)
    {
        this(id, url, pool, version, type, status.or(Status.unavailable), ImmutableMap.<String, Link>of());
    }

    public Service(URI url,
                   String pool,
                   Version version,
                   String type,
                   Optional<Status> status)
    {
        this(Optional.<String>absent(), url, pool, version, type, status.or(Status.unavailable), ImmutableMap.<String, Link>of());
    }

    Service(final Optional<String> id,
            final URI url,
            final String pool,
            final Version version,
            final String type,
            final Status status,
            final Map<String, Link> links)
    {
        this.id = id;
        this.url = url;
        this.pool = pool;
        this.version = version;
        this.type = type;
        this.links = links;
        this.status = status;
    }

    public Optional<String> getId()
    {
        return id;
    }

    public Status getStatus()
    {
        return status;
    }

    public URI getUrl()
    {
        return url;
    }

    public String getPool()
    {
        return pool;
    }

    public Version getVersion()
    {
        return version;
    }

    public String getType()
    {
        return type;
    }

    @JsonProperty("_links")
    public Map<String, Link> getLinks()
    {
        return links;
    }

    public Service withId(final String id)
    {
        return new Service(Optional.of(id),
                           getUrl(),
                           getPool(),
                           getVersion(),
                           getType(),
                           getStatus(),
                           ImmutableMap.of("self", new Link(URI.create("/srv/" + id)),
                                           "status", new Link(URI.create("/srv/" + id + "/status")))
        );

    }

    @Override
    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(final Object obj)
    {
        return EqualsBuilder.reflectionEquals(this, obj);
    }
}
