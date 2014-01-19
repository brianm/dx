package io.xn.dx.jaxrs;

import com.google.common.collect.ImmutableSet;

import javax.inject.Inject;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Feature;
import java.util.Set;

public class DaggerApplication extends Application
{
    private final Set<Object> singletons;

    @Inject
    public DaggerApplication(Set<Feature> features,
                             @Resources Set<Object> resources,
                             @Providers Set<Object> providers)
    {
        this.singletons = ImmutableSet.builder()
                                      .addAll(features)
                                      .addAll(resources)
                                      .addAll(providers)
                                      .build();


    }

    @Override
    public Set<Object> getSingletons()
    {
        return singletons;
    }
}
