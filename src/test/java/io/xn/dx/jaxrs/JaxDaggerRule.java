package io.xn.dx.jaxrs;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import io.undertow.Undertow;
import io.xn.dx.NetUtil;
import io.xn.dx.server.DxServerModule;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.junit.rules.ExternalResource;
import retrofit.RestAdapter;

import javax.inject.Named;
import javax.inject.Singleton;
import java.net.URI;

import static java.lang.String.format;

public class JaxDaggerRule extends ExternalResource
{

    private final int port = NetUtil.findUnusedPort();
    private UndertowJaxrsServer ut;
    private URI baseUri;
    private RestAdapter adapter;

    @Override
    protected void before() throws Throwable
    {
        ut = new UndertowJaxrsServer();
        DaggerApplication app = ObjectGraph.create(new TestModule()).get(DaggerApplication.class);
        ut.deploy(app);
        ut.start(Undertow.builder().addHttpListener(port, "127.0.0.1"));

        baseUri = URI.create(format("http://localhost:%d/", port));
        adapter = new RestAdapter.Builder().setEndpoint(baseUri.toString()).build();
    }

    public URI getBaseUri()
    {
        return baseUri;
    }

    public <T> T retrofit(Class<T> clientType)
    {
        return adapter.create(clientType);
    }

    @Override
    protected void after()
    {
        ut.stop();
    }

    @Module(includes = {DxServerModule.class, DaggerApplicationDefaults.class}, injects = DaggerApplication.class)
    public static class TestModule
    {
        @Provides
        @Singleton
        @Named("message")
        public String getMessage()
        {
            return "testing!";
        }
    }
}
