package io.xn.dx.server;

import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import io.undertow.Undertow;
import io.xn.dx.AvailablePortFinder;
import io.xn.dx.jaxrs.DaggerApplication;
import io.xn.dx.jaxrs.DaggerApplicationDefaults;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skife.jetty.v9.client.HttpClient;
import org.skife.jetty.v9.client.api.ContentResponse;

import javax.inject.Named;
import javax.inject.Singleton;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

public class RootTest
{

    private static final int port = AvailablePortFinder.getNextAvailable();
    private static UndertowJaxrsServer ut;
    private static HttpClient http;
    private static URI baseUri;

    @BeforeClass
    public static void setUpClass() throws Exception
    {
        ut = new UndertowJaxrsServer();
        DaggerApplication app = ObjectGraph.create(new TestModule()).get(DaggerApplication.class);
        ut.deploy(app);
        ut.start(Undertow.builder().addListener(port, "127.0.0.1"));

        baseUri = URI.create(format("http://localhost:%d/", port));
        http = new HttpClient();
        http.start();
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
        http.stop();
        ut.stop();
    }

    @Test
    public void testFoo() throws Exception
    {
        Stopwatch sw = new Stopwatch();
        sw.start();
        http.GET(baseUri.resolve("/suspend?time=2000")).getContentAsString();
        sw.stop();
        assertThat(sw.elapsed(TimeUnit.MILLISECONDS)).isGreaterThan(2000);
    }

    @Test
    public void testBar() throws Exception
    {
        ContentResponse out = http.GET(baseUri.resolve("/suspend"));
        assertThat(out.getStatus()).isEqualTo(400);
        System.out.println(out.getContentAsString());
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
