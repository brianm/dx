package io.xn.dx.jaxrs;

import com.google.common.collect.ImmutableSet;

import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Feature;
import java.util.Set;

@ApplicationPath("/")
public class DaggerApplication extends Application
{
    private final Set<Object> singletons;

    @Inject
    public DaggerApplication(Set<Feature> features,
                             @JaxrsResource Set<Object> resources,
                             @JaxrsResource Set<Object> providers)
    {
        ImmutableSet.Builder<Object> builder = ImmutableSet.<Object>builder();
        builder.addAll(features);
        builder.addAll(resources);
        builder.addAll(providers);
        this.singletons = builder.build();

    }

    @Override
    public Set<Object> getSingletons()
    {
        return singletons;
    }
}
