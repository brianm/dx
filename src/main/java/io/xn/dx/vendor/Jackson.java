package io.xn.dx.vendor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.datatype.guava.GuavaModule;

import java.io.IOException;


public class Jackson
{
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.registerModule(new GuavaModule());
        mapper.registerModule(new SemverModel());
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        // require explicit json fields
//        mapper.disable(MapperFeature.AUTO_DETECT_CREATORS);
//        mapper.disable(MapperFeature.AUTO_DETECT_FIELDS);
//        mapper.disable(MapperFeature.AUTO_DETECT_SETTERS);
//        mapper.disable(MapperFeature.AUTO_DETECT_GETTERS);
//        mapper.disable(MapperFeature.AUTO_DETECT_IS_GETTERS);
//        mapper.disable(MapperFeature.USE_GETTERS_AS_SETTERS);
//        mapper.disable(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS);
//        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        // use ISO dates
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // skip null fields
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static ObjectMapper getMapper()
    {
        return mapper;
    }

    public static ObjectWriter getWriter()
    {
        return mapper.writer();
    }

    public static ObjectReader getReader()
    {
        return mapper.reader();
    }

    public static class SemverModel extends Module
    {
        @Override
        public String getModuleName()
        {
            return "version";
        }

        @Override
        public com.fasterxml.jackson.core.Version version()
        {
            return com.fasterxml.jackson.core.Version.unknownVersion();
        }

        @Override
        public void setupModule(final SetupContext ctx)
        {
            ctx.addSerializers(new Serializers.Base()
            {
                @Override
                public JsonSerializer<?> findSerializer(final SerializationConfig config, final JavaType type, final BeanDescription beanDesc)
                {
                    if (type.getRawClass().equals(com.github.zafarkhaja.semver.Version.class)) {
                        return new JsonSerializer<com.github.zafarkhaja.semver.Version>()
                        {
                            @Override
                            public void serialize(final com.github.zafarkhaja.semver.Version value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonProcessingException
                            {
                                jgen.writeString(value.toString());
                            }
                        };
                    }
                    return super.findSerializer(config, type, beanDesc);
                }
            });
        }
    }
}
