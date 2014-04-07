package io.xn.dx.reps;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.common.collect.Maps;

import java.net.URI;
import java.util.Map;

public class HalContainer
{
    private final Map<String, Link> links = Maps.newConcurrentMap();
    private final Object entity;

    public HalContainer(Map<String, Link> links, Object entity) {
        this.links.putAll(links);
        this.entity = entity;
    }

    public HalContainer(Object entity) {
        this.entity = entity;
    }

    public HalContainer addLink(String rel, URI href) {
        this.links.put(rel, new Link(href));
        return this;
    }

    @JsonProperty("_links")
    public Map<String, Link> getLinks()
    {
        return links;
    }

    @JsonUnwrapped
    public Object getEntity()
    {
        return entity;
    }
}
