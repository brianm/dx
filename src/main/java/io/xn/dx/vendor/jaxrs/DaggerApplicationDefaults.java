package io.xn.dx.vendor.jaxrs;

import dagger.Module;
import dagger.Provides;

import javax.ws.rs.core.Feature;
import java.util.Collections;
import java.util.Set;

@Module(injects = DaggerApplication.class, complete = false, library = true)
public final class DaggerApplicationDefaults
{
    @Provides(type= Provides.Type.SET_VALUES)
    public Set<Feature> features() {
        return Collections.emptySet();
    }

    @Provides(type= Provides.Type.SET_VALUES)
    @Resources
    public Set<Object> resources() {
        return Collections.emptySet();
    }

    @Provides(type= Provides.Type.SET_VALUES)
    @Providers
    public Set<Object> providers() {
        return Collections.emptySet();
    }
}
