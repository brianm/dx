package io.xn.dx.vendor;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import io.undertow.Undertow;
import io.xn.dx.NetUtil;
import io.xn.dx.server.DxServerModule;
import io.xn.dx.vendor.jaxrs.DaggerApplication;
import io.xn.dx.vendor.jaxrs.DaggerApplicationDefaults;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.junit.rules.ExternalResource;

import javax.inject.Named;
import javax.inject.Singleton;
import java.net.URI;

import static java.lang.String.format;

public class JaxDaggerRule extends ExternalResource
{

    private final int port = NetUtil.findUnusedPort();
    private UndertowJaxrsServer ut;
    private URI baseUri;

    @Override
    protected void before() throws Throwable
    {
        ut = new UndertowJaxrsServer();
        DaggerApplication app = ObjectGraph.create(new TestModule()).get(DaggerApplication.class);
        ut.deploy(app);
        ut.start(Undertow.builder().addHttpListener(port, "127.0.0.1"));

        baseUri = URI.create(format("http://localhost:%d/", port));
    }

    public URI getBaseUri()
    {
        return baseUri;
    }

    public URI resolve(String path) {
        return getBaseUri().resolve(path);
    }

    @Override
    protected void after()
    {
        ut.stop();
    }

    @Module(includes = {DxServerModule.class, DaggerApplicationDefaults.class}, injects = DaggerApplication.class)
    public static class TestModule
    {

    }
}
