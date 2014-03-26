package io.xn.dx.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Stopwatch;
import io.xn.dx.vendor.Jackson;
import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import io.undertow.Undertow;
import io.xn.dx.NetUtil;
import io.xn.dx.vendor.jaxrs.DaggerApplication;
import io.xn.dx.vendor.jaxrs.DaggerApplicationDefaults;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skife.jetty.v9.client.HttpClient;
import org.skife.jetty.v9.client.api.ContentResponse;

import javax.inject.Named;
import javax.inject.Singleton;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import static io.xn.dx.assertions.GdxAssertions.assertThat;
import static java.lang.String.format;

public class RootTest
{

    private static int port;
    private static UndertowJaxrsServer ut;
    private static HttpClient http;
    private static URI baseUri;

    @BeforeClass
    public static void setUpClass() throws Exception
    {
        ut = new UndertowJaxrsServer();
        DaggerApplication app = ObjectGraph.create(new TestModule()).get(DaggerApplication.class);
        ut.deploy(app);
        port = NetUtil.findUnusedPort();
        ut.start(Undertow.builder().addHttpListener(port, "127.0.0.1"));

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
        JsonNode node = Jackson.getReader().readTree(out.getContentAsString());
        assertThat(node).isArray();
        assertThat(node).hasSize(1);
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
