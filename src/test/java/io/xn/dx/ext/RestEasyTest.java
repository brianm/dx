package io.xn.dx.ext;

import com.google.common.collect.ImmutableSet;
import io.undertow.Undertow;
import io.xn.dx.NetUtil;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.junit.Test;
import org.skife.jetty.v9.client.HttpClient;
import org.skife.jetty.v9.client.api.ContentResponse;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

public class RestEasyTest
{

    @Test
    public void testFoo() throws Exception
    {
        int port = NetUtil.findUnusedPort();
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

    @Test
    public void testLinkHeader() throws Exception
    {
        int port = NetUtil.findUnusedPort();
        UndertowJaxrsServer ut = new UndertowJaxrsServer();

        ut.deploy(MyApplication.class);
        ut.start(Undertow.builder().addListener(port, "0.0.0.0"));

        HttpClient http = new HttpClient();
        http.start();

        ContentResponse c = http.GET(format("http://127.0.0.1:%d/header", port));
        assertThat(c.getHeaders().containsKey("Link")).isTrue();
        String val = c.getHeaders().get("Link");
        assertThat(val).isEqualTo("</header/ttl>; rel=\"ttl\"");

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
        @Path("/header")
        @Produces("text/plain")
        public Response linkHeader()
        {
            return Response.ok("hello world", MediaType.TEXT_PLAIN)
                           .link("/header/ttl", "ttl")
                           .build();
        }


        @GET
        @Produces("text/plain")
        public String get()
        {
            return "hello world";
        }
    }
}
