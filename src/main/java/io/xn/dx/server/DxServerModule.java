package io.xn.dx.server;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.collect.ImmutableSet;
import dagger.Module;
import dagger.Provides;
import io.xn.dx.storage.InMemoryStorage;
import io.xn.dx.storage.Storage;
import io.xn.dx.vendor.Jackson;
import io.xn.dx.vendor.jaxrs.DaggerApplication;
import io.xn.dx.vendor.jaxrs.Providers;
import io.xn.dx.vendor.jaxrs.Resources;
import io.xn.dx.vendor.jaxrs.ValidationExceptionMapper;
import org.jboss.resteasy.plugins.validation.ValidatorContextResolver;

import javax.inject.Singleton;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@Module(complete = false, injects = DaggerApplication.class)
public class DxServerModule
{
    @Provides
    @Singleton
    public Storage storage()
    {
        return new InMemoryStorage( new ScheduledThreadPoolExecutor(2));
    }

    @Provides(type = Provides.Type.SET_VALUES)
    @Resources
    @Singleton
    public Set<Object> resources(Root root, ServiceResource sr)
    {
        return ImmutableSet.of(root, sr);
    }

    @Provides(type = Provides.Type.SET_VALUES)
    @Providers
    @Singleton
    public Set<Object> providers()
    {
        return ImmutableSet.of(new JacksonJsonProvider(Jackson.getMapper()),
                               new ValidatorContextResolver(),
                               new ValidationExceptionMapper());
    }
}
