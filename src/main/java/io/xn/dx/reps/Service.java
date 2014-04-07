package io.xn.dx.reps;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.airlift.units.Duration;
import io.xn.dx.version.Version;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.net.URI;
import java.util.Map;
import java.util.Set;

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
    private final Optional<Duration> ttl;

    @JsonCreator
    public Service(@JsonProperty("id") Optional<String> id,
                   @JsonProperty("url") URI url,
                   @JsonProperty("pool") String pool,
                   @JsonProperty("version") Version version,
                   @JsonProperty("type") String type,
                   @JsonProperty("status") Optional<Status> status,
                   @JsonProperty("ttl") Optional<Duration> ttl)
    {
        this(id, url, pool, version, type, status.or(Status.unavailable), ttl);
    }

    public Service(URI url,
                   String pool,
                   Version version,
                   String type,
                   Optional<Status> status)
    {
        this(Optional.<String>absent(),
             url,
             pool,
             version,
             type,
             status.or(Status.unavailable),
             Optional.<Duration>absent());
    }

    Service(final Optional<String> id,
            final URI url,
            final String pool,
            final Version version,
            final String type,
            final Status status,
            final Optional<Duration> ttl)
    {
        Map<String, Link> links = Maps.newConcurrentMap();

        this.id = id;
        if (id.isPresent()) {
            // add links, this is gross, but whatever
            links.put("self", new Link(URI.create("/srv/" + id.get())));
            links.put("status", new Link(URI.create("/srv/" + id.get() + "/status")));
        }
        this.ttl = ttl;
        if (ttl.isPresent()) {
            links.put("ttl", new Link(URI.create("/srv/" + id.get() + "/ttl")));
        }
        this.url = url;
        this.pool = pool;
        this.version = version;
        this.type = type;
        this.status = status;
        this.links = links;
    }

    /**
     * Solely for creating full urls in links
     */
    private Service(final Optional<String> id,
            final URI url,
            final String pool,
            final Version version,
            final String type,
            final Status status,
            final Optional<Duration> ttl,
            final Map<String, Link> links)
    {
        this.id = id;
        this.ttl = ttl;
        this.url = url;
        this.pool = pool;
        this.version = version;
        this.type = type;
        this.status = status;
        this.links = links;
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
                           getTtl());
    }

    public Service withResolvedLinkUris(URI base, String... rels) {
        ImmutableMap.Builder<String, Link> new_links = ImmutableMap.builder();
        Set<String> to_resolve = Sets.newHashSet(rels);
        for (String rel : this.links.keySet()) {
            Link link = this.links.get(rel);
            if (to_resolve.contains(rel)) {
                Link new_link = new Link(base.resolve(link.getHref()));
                new_links.put(rel, new_link);
            }
            else {
                new_links.put(rel, link);
            }
        }
        return new Service(id, url, pool, version, type, status, ttl, new_links.build());
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

    public Optional<Duration> getTtl()
    {
        return ttl;
    }
}
