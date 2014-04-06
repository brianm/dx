package io.xn.dx.reps;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.net.URI;
import java.util.Objects;

public class Link
{
    private final URI href;

    @JsonCreator
    public Link(@JsonProperty("href") final URI href)
    {
        this.href = href;
    }

    public URI getHref()
    {
        return href;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(href);
    }

    @Override
    public boolean equals(final Object obj)
    {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }
}
