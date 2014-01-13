package io.xn.dx.server;

import com.google.common.collect.ImmutableSet;
import dagger.Module;
import dagger.Provides;
import io.xn.dx.jaxrs.DaggerApplication;
import io.xn.dx.jaxrs.JaxrsResource;

import javax.inject.Singleton;
import java.util.Set;

@Module(complete = false, injects = DaggerApplication.class)
public class DxServerModule
{
    @Provides(type = Provides.Type.SET_VALUES)
    @JaxrsResource
    @Singleton
    public Set<Object> getRoots(Root root) {
        return ImmutableSet.<Object>of(root);
    }
}
