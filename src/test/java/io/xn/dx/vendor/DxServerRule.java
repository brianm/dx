package io.xn.dx.vendor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import dagger.Module;
import dagger.ObjectGraph;
import io.airlift.units.Duration;
import io.undertow.Undertow;
import io.xn.dx.NetUtil;
import io.xn.dx.ext.JacksonEntity;
import io.xn.dx.reps.Service;
import io.xn.dx.reps.Status;
import io.xn.dx.server.DxServerModule;
import io.xn.dx.vendor.jaxrs.DaggerApplication;
import io.xn.dx.vendor.jaxrs.DaggerApplicationDefaults;
import io.xn.dx.reps.Version;
import org.apache.http.client.fluent.Request;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.junit.rules.ExternalResource;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;

public class DxServerRule extends ExternalResource
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

    public URI resolve(String path)
    {
        return getBaseUri().resolve(path);
    }

    @Override
    protected void after()
    {
        ut.stop();
    }

    public Service createService(final URI uri, final String type, final Version version, final String pool, final Optional<Status> status) throws IOException
    {
        Map<String, String> body = Maps.newHashMap();
        body.put("url", uri.toString());
        body.put("type", type);
        body.put("version", version.toString());
        body.put("pool", pool);
        if (status.isPresent()) {
            body.put("status", status.get().toString());
        }
        String json = Request.Post(resolve("/srv"))
                             .body(new JacksonEntity(body))
                             .execute()
                             .returnContent().asString();
        return Jackson.getMapper().readValue(json, Service.class);
    }

    public JsonNode GET(final URI uri) throws IOException
    {
        return Request.Get(getBaseUri().resolve(uri))
                      .execute()
                      .handleResponse(new JsonNodeHandler());
    }

    public JsonNode POST(final URI uri, final Object entity) throws IOException
    {
        return Request.Post(getBaseUri().resolve(uri))
                      .body(new JacksonEntity(entity))
                      .execute()
                      .handleResponse(new JsonNodeHandler());
    }

    public int DELETE(final URI uri) throws IOException
    {
        return Request.Delete(getBaseUri().resolve(uri)).execute().returnResponse().getStatusLine().getStatusCode();
    }

    public Service createService(final URI uri, final String type, final Version version, final String pool, final Duration ttl) throws IOException
    {
        Map<String, String> body = Maps.newHashMap();
        body.put("url", uri.toString());
        body.put("type", type);
        body.put("version", version.toString());
        body.put("pool", pool);
        body.put("ttl", ttl.toString());
        String json = Request.Post(resolve("/srv"))
                             .body(new JacksonEntity(body))
                             .execute()
                             .returnContent().asString();
        return Jackson.getMapper().readValue(json, Service.class);
    }

    @Module(includes = {DxServerModule.class, DaggerApplicationDefaults.class}, injects = DaggerApplication.class)
    public static class TestModule
    {

    }
}
