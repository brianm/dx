package io.xn.dx;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

public class Descriptor
{
    private final Optional<String> id;
    private final Optional<URI> self;
    private final Optional<URI> healthCheckUri;
    private final URI uri;
    private final Map<String, Object> fields = new LinkedHashMap<>();

    public Descriptor(Optional<String> id,
                      Optional<URI> self,
                      Optional<URI> healthCheckUri,
                      URI uri,
                      Map<String, Object> fields)
    {
        this.id = id;
        this.self = self;
        this.healthCheckUri = healthCheckUri;
        this.uri = uri;
        this.fields.putAll(fields);
    }

    @JsonCreator
    public Descriptor(@JsonProperty("_id") Optional<String> id,
                      @JsonProperty("_self") Optional<URI> self,
                      @JsonProperty("healthCheckUri") Optional<URI> healthCheckUri,
                      @JsonProperty("uri") URI uri)
    {
        this.id = id;
        this.self = self;
        this.healthCheckUri = healthCheckUri;
        this.uri = uri;
    }

    @JsonAnySetter
    public void set(String key, Object value)
    {
        Preconditions.checkState(!key.startsWith("_"), "May not have a key named '" + key + "'");

        fields.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, Object> getFields()
    {
        return ImmutableMap.copyOf(fields);
    }

    @JsonProperty("_id")
    public Optional<String> getInstanceId()
    {
        return id;
    }

    @JsonIgnore
    public String getId()
    {
        return id.get();
    }

    public Optional<URI> getHealthCheckUri()
    {
        return healthCheckUri;
    }

    public URI getUri()
    {
        return uri;
    }

    @JsonProperty("_self")
    public Optional<URI> getSelf()
    {
        return self;
    }


    public Descriptor withId(final String id)
    {
        return new Descriptor(Optional.fromNullable(id), getSelf(), getHealthCheckUri(), getUri(), getFields());
    }

    public Descriptor withSelf(final URI uri)
    {
        return new Descriptor(getInstanceId(), Optional.fromNullable(uri), getHealthCheckUri(), getUri(), getFields());
    }
}
