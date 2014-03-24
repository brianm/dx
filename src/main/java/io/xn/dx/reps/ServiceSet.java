package io.xn.dx.reps;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class ServiceSet
{
    /*
    {
      _links: {
          self: { href: "/srv?type=memcached" },
          delta: { href: "/srv?type=memcached&delta=17"}
      },
      services: [
        {
            _links: {
              self: { href: "/srv/123abc" },
              status: { href: "/srv/123abc/status" },
            },
            status: "ok",
            type: "memcached",
            url: "memcached://10.0.1.100:11211",
            pool: "general",
            version: "0.2.3"
        },
        {
            _links: {
              self: { href: "/srv/z7h6" },
              status: { href: "/srv/z7h6/status" },
            },
            status: "ok",
            type: "memcached",
            url: "memcached://10.0.1.101:11211",
            pool: "blue",
            version: "0.2.2"
        }
      ]
    }
    */

    private final Map<String, Link> _links;
    private final ImmutableSet<Service> services;

    public ServiceSet(final Map<String, Link> links, Iterable<Service> services)
    {
        _links = links;
        this.services = ImmutableSet.copyOf(services);
    }


    public static ServiceSet build(Map<String, String> filters, String deltaKey, final Iterable<Service> services)
    {
        SortedMap<String, String> sorted = new TreeMap<>(filters);
        URI base = URI.create("/srv");
        UriBuilder builder = UriBuilder.fromUri(base);
        for (Map.Entry<String, String> entry : sorted.entrySet()) {
            builder.queryParam(entry.getKey(), entry.getValue());
        }
        URI self = builder.build();

        URI delta_uri = builder.queryParam("delta", deltaKey).build();

        return new ServiceSet(ImmutableMap.of("self", new Link(self),
                                              "delta", new Link(delta_uri)),
                              services);
    }

    @JsonProperty("_links")
    public Map<String, Link> getLinks()
    {
        return _links;
    }

    @JsonProperty("services")
    public Set<Service> getServices()
    {
        return services;
    }
}
