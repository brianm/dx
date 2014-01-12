package io.xn.dx;

import com.google.common.collect.ImmutableSet;
import io.undertow.Undertow;
import io.undertow.client.UndertowClient;
import io.undertow.client.http.HttpClientConnection;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.junit.Test;
import org.skife.jetty.v9.client.HttpClient;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import java.util.Set;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

public class RestEasyTest
{

    @Test
    public void testFoo() throws Exception
    {
        int port = AvailablePortFinder.getNextAvailable();
        UndertowJaxrsServer ut = new UndertowJaxrsServer();

        ut.deploy(MyApplication.class);
        ut.start(Undertow.builder().addListener(port, "0.0.0.0"));

        HttpClient http = new HttpClient();
        http.start();

        String c = http.GET(format("http://127.0.0.1:%d/", port)).getContentAsString();
        assertThat(c).isEqualTo("hello world");

        http.stop();
        ut.stop();
    }

    @ApplicationPath("/")
    public static final class MyApplication extends Application
    {
        @Override
        public Set<Class<?>> getClasses()
        {
            return ImmutableSet.<Class<?>>of(Root.class);
        }
    }

    @Path("/")
    public static final class Root
    {
        @GET
        @Produces("text/plain")
        public String get()
        {
            return "hello world";
        }
    }
}
