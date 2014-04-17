package io.xn.dx.reps;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.IOException;

@JsonSerialize(using = Status.Serializer.class)
@JsonDeserialize(using = Status.Desrializer.class)
public class Status
{
    public static final Status ok = new Status("ok");
    public static final Status unknown = new Status("unknown");
    public static final Status expired = new Status("expired");

    private final String status;

    private Status(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    @Override
    public String toString()
    {
        return status;
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

    public static Status valueOf(final String value)
    {
        return new Status(value);
    }

    public static class Desrializer extends JsonDeserializer<Status> {

        @Override
        public Status deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException
        {
            String s = jp.readValueAs(String.class);
            return new Status(s);
        }
    }

    public static class Serializer extends JsonSerializer<Status>
    {
        @Override
        public void serialize(final Status value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonProcessingException
        {
            jgen.writeString(value.getStatus());
        }
    }
}
