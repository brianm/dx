package io.xn.dx.reps;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.zafarkhaja.semver.Version;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

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
    private final Map<String, Link> links;

    @JsonCreator
    public Service(@JsonProperty("url") URI url,
                   @JsonProperty("pool") String pool,
                   @JsonProperty("version") Version version,
                   @JsonProperty("type") String type,
                   @JsonProperty("status") Optional<Status> status)
    {
        this(url, pool, version, type, status.or(Status.unavailable), ImmutableMap.<String, Link>of());
    }

    Service(final URI url,
            final String pool,
            final Version version,
            final String type,
            final Status status,
            final Map<String, Link> links)
    {
        this.url = url;
        this.pool = pool;
        this.version = version;
        this.type = type;
        this.links = links;
        this.status = status;
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
        return new Service(getUrl(),
                           getPool(),
                           getVersion(),
                           getType(),
                           getStatus(),
                           ImmutableMap.of("self", new Link(URI.create("/srv/" + id)),
                                           "status", new Link(URI.create("/srv/" + id + "/status")))
        );

    }
}
