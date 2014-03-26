package io.xn.dx.version;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@JsonSerialize(using = Version.JacksonSerializer.class)
@JsonDeserialize(using = Version.JacksonDeserializer.class)
public class Version
{
    public static Pattern VERSION_PATTERN = Pattern.compile("(\\d+)              #major \n" +
                                                            "(?:\\.(\\d+))?      #minor \n" +
                                                            "(?:\\.(\\d+))?      #patch \n" +
                                                            "(?:-([\\w\\.]+))?   #prerelease \n" +
                                                            "(?:\\+([\\w\\.]+))? #buildnumber",
                                                            Pattern.COMMENTS
    );
    private final int major;
    private final Optional<Integer> minor;
    private final Optional<Integer> patch;
    private final Optional<String> pre;
    private final Optional<String> build;

    public Version(final int major,
                   final Optional<Integer> minor,
                   final Optional<Integer> patch,
                   final Optional<String> pre,
                   final Optional<String> build)
    {


        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.pre = pre;
        this.build = build;
    }

    public int getMajor()
    {
        return major;
    }

    public Optional<Integer> getMinor()
    {
        return minor;
    }

    public Optional<Integer> getPatch()
    {
        return patch;
    }

    public Optional<String> getPre()
    {
        return pre;
    }

    public Optional<String> getBuild()
    {
        return build;
    }

    public static Version valueOf(final String value)
    {
        Matcher m = VERSION_PATTERN.matcher(value);
        if (!m.matches()) {
            throw new IllegalVersionException(value);
        }
        int major = Integer.parseInt(m.group(1));
        Optional<Integer> minor = Optional.fromNullable(m.group(2)).transform(ATOI);
        Optional<Integer> patch = Optional.fromNullable(m.group(3)).transform(ATOI);
        Optional<String> pre = Optional.fromNullable(m.group(4));
        Optional<String> build = Optional.fromNullable(m.group(5));

        return new Version(major, minor, patch, pre, build);
    }

    public String toString()
    {
        StringBuilder builder = new StringBuilder().append(getMajor());
        if (getMinor().isPresent()) {
            builder.append(".").append(getMinor().get());
            if (getPatch().isPresent()) {
                builder.append(".").append(getPatch().get());
            }
        }
        if (getPre().isPresent()) {
            builder.append("-").append(getPre().get());
        }
        if (getBuild().isPresent()) {
            builder.append("+").append(getBuild().get());
        }

        return builder.toString();
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

    private static Function<String, Integer> ATOI = new Function<String, Integer>()
    {
        @Override
        public Integer apply(final String input)
        {
            return Integer.parseInt(input);
        }
    };

    public boolean satisfies(final Version that)
    {
        // majors are compat
        if (this.getMajor() != that.getMajor()) {
            return false;
        }

        // this.minor < that.minor
        if (that.getMinor().isPresent()) {
            if (this.getMinor().isPresent()) {
                int this_minor = this.getMinor().get();
                int that_minor = that.getMinor().get();
                if (this_minor < that_minor) {
                    return false;
                }
            }
            else {
                return false;
            }
        }

        // this.patch < that.patch
        if (that.getPatch().isPresent()) {
            if (this.getPatch().isPresent()) {
                int this_patch = this.getPatch().get();
                int that_patch = that.getPatch().get();
                if (this_patch < that_patch) {
                    return false;
                }
            }
            else {
                return false;
            }
        }

        return true;
    }

    public static class JacksonSerializer extends JsonSerializer<Version>
    {

        @Override
        public void serialize(final Version value,
                              final JsonGenerator jgen,
                              final SerializerProvider provider) throws IOException, JsonProcessingException
        {
            jgen.writeString(value.toString());
        }
    }

    public static class JacksonDeserializer extends JsonDeserializer<Version>
    {

        @Override
        public Version deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException
        {
            String s = jp.getValueAsString();
            return Version.valueOf(s);
        }
    }
}
