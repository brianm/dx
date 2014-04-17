package io.xn.dx.ext;

import com.google.common.collect.ImmutableSet;
import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import io.xn.dx.NetUtil;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.net.InetSocketAddress;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class DaggerTest
{
    private MyApp app;
    private ObjectGraph graph;

    @Before
    public void setUp() throws Exception
    {
        graph = ObjectGraph.create(new MyModule(), new PluginTwoModule());
        app = graph.get(MyApp.class);
    }

    @Test
    public void testWeGotAnApp() throws Exception
    {
        assertThat(app).isNotNull();
    }

    @Test
    public void testGetAddress() throws Exception
    {
        assertThat(app.getListenerAt().getHostName()).isEqualTo("0.0.0.0");
    }

    @Test
    public void testPlugins() throws Exception
    {
        assertThat(app.getPlugins()).containsExactly(new NamedPlugin("one"), new NamedPlugin("two"));
    }

    public static class MyApp
    {
        private final InetSocketAddress listenerAt;
        private final Set<Plugin> plugins;

        @Inject
        public MyApp(InetSocketAddress listenerAt, Set<Plugin> plugins)
        {
            this.listenerAt = listenerAt;
            this.plugins = ImmutableSet.copyOf(plugins);
        }

        public InetSocketAddress getListenerAt()
        {
            return listenerAt;
        }

        public Set<Plugin> getPlugins()
        {
            return plugins;
        }
    }

    public static interface Plugin
    {
        public abstract String getName();
    }

    public static class NamedPlugin implements Plugin
    {
        private final String name;

        public NamedPlugin(String name)
        {
            this.name = name;
        }

        @Override
        public String getName()
        {
            return name;
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
    }

    @Module(injects = MyApp.class)
    public static class MyModule
    {
        @Provides
        @Singleton
        @Named("port")
        public Integer port()
        {
            return NetUtil.findUnusedPort();
        }

        @Provides
        @Named("host")
        public String host()
        {
            return "0.0.0.0";
        }

        @Provides
        public InetSocketAddress listenAt(@Named("host") String host, @Named("port") Integer port)
        {
            return new InetSocketAddress(host, port);
        }

        @Provides(type = Provides.Type.SET)
        @Singleton
        public Plugin getPluginOne()
        {
            return new NamedPlugin("one");
        }
    }

    @Module(library = true)
    public static class PluginTwoModule
    {
        @Provides(type = Provides.Type.SET_VALUES)
        public Set<Plugin> pluginTwo()
        {
            // can do classpath scanning, for instance :-)
            return ImmutableSet.<Plugin>of(new NamedPlugin("two"));
        }
    }

}
