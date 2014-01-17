package io.xn.dx.server;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.collect.ImmutableSet;
import dagger.Module;
import dagger.Provides;
import io.xn.dx.jaxrs.DaggerApplication;
import io.xn.dx.jaxrs.JaxrsProvider;
import io.xn.dx.jaxrs.JaxrsResource;
import io.xn.dx.storage.InMemoryStorage;
import io.xn.dx.storage.Storage;

import javax.inject.Singleton;
import java.util.Set;

@Module(complete = false, injects = DaggerApplication.class)
public class DxServerModule
{
    @Provides
    @Singleton
    public Storage storage() {
        return new InMemoryStorage();
    }

    @Provides(type = Provides.Type.SET_VALUES)
    @JaxrsResource
    @Singleton
    public Set<Object> resources(Root root, ServiceResource sr) {
        return ImmutableSet.of(root, sr);
    }

    @Provides(type= Provides.Type.SET_VALUES)
    @JaxrsProvider
    @Singleton
    public Set<Object> providers() {
        ObjectMapper factory = new ObjectMapper();
        factory.registerModule(new GuavaModule());
        factory.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        factory.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        factory.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        factory.configure(SerializationFeature.INDENT_OUTPUT, true);

        // require explicit json fields
//        factory.disable(MapperFeature.AUTO_DETECT_CREATORS);
//        factory.disable(MapperFeature.AUTO_DETECT_FIELDS);
//        factory.disable(MapperFeature.AUTO_DETECT_SETTERS);
//        factory.disable(MapperFeature.AUTO_DETECT_GETTERS);
//        factory.disable(MapperFeature.AUTO_DETECT_IS_GETTERS);
//        factory.disable(MapperFeature.USE_GETTERS_AS_SETTERS);
//        factory.disable(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS);
//        factory.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        // use ISO dates
        factory.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // skip null fields
        factory.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return ImmutableSet.<Object>of(new JacksonJsonProvider(factory));
    }
}
