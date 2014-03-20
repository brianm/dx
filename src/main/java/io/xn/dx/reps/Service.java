package io.xn.dx.reps;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    private final String version;
    private final String type;
    private final Map<String, Link> links;

    @JsonCreator
    public Service(@JsonProperty("url") URI url,
                   @JsonProperty("pool") String pool,
                   @JsonProperty("version") String version,
                   @JsonProperty("type") String type)
    {
        this(url, pool, version, type, ImmutableMap.<String, Link>of());
    }

    Service(final URI url, final String pool, final String version, final String type, final Map<String, Link> links)
    {
        this.url = url;
        this.pool = pool;
        this.version = version;
        this.type = type;
        this.links = links;
    }

    public URI getUrl()
    {
        return url;
    }

    public String getPool()
    {
        return pool;
    }

    public String getVersion()
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
                           ImmutableMap.of("self", new Link(URI.create("/srv/" + id)),
                                           "status", new Link(URI.create("/srv/" + id + "/status")))
        );

    }
}
