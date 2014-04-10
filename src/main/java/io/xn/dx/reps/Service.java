package io.xn.dx.reps;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import io.airlift.units.Duration;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.net.URI;
import java.util.Map;

public class Service
{
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
        this(id, url, pool, version, type, status.or(Status.unknown), ttl);
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
            links.put("heartbeat", new Link(URI.create("/srv/" + id.get() + "/heartbeat")));
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

    public Service withHeartBeatBaseUri(final URI ttlBaseUri)
    {
        if (ttl.isPresent()) {
            Link hb_link = this.getLinks().get("heartbeat");
            Link resolved_hb_link = new Link(ttlBaseUri.resolve(hb_link.getHref()));
            Map<String, Link> new_links = ImmutableMap.<String, Link>builder()
                                                      .putAll((Maps.filterKeys(links, (s) -> !"heartbeat".equals(s))))
                                                      .put("heartbeat", resolved_hb_link)
                                                      .build();
            return new Service(id,
                               getUrl(),
                               getPool(),
                               getVersion(),
                               getType(),
                               getStatus(),
                               getTtl(),
                               new_links);
        }
        else
        {
            return this;
        }
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

    public Service withStatus(final Status status)
    {
        return new Service(id, url, pool, version, type, status, ttl, links);
    }
}
