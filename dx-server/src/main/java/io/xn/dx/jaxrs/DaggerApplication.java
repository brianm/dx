package io.xn.dx.jaxrs;

import com.google.common.collect.ImmutableSet;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Feature;
import java.util.Set;

@ApplicationPath("/")
public class DaggerApplication extends Application
{
    private final Set<Object> singletons;
    private String bindHost;
    private int bindPort;

    @Inject
    public DaggerApplication(Set<Feature> features,
                             @JaxrsResource Set<Object> resources,
                             @JaxrsResource Set<Object> providers,
                             @Named("bind-host") String bindHost,
                             @Named("bind-port") Integer bindPort)
    {
        this.bindHost = bindHost;
        this.bindPort = bindPort;
        ImmutableSet.Builder<Object> builder = ImmutableSet.builder();
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

    public String getBindHost()
    {
        return bindHost;
    }

    public int getBindPort()
    {
        return bindPort;
    }
}
