package io.xn.dx.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.collect.ImmutableMap;
import io.xn.dx.Json;
import io.xn.dx.ext.JacksonEntity;
import io.xn.dx.vendor.Jackson;
import io.xn.dx.vendor.JaxDaggerRule;
import io.xn.dx.version.Version;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;

import static io.xn.dx.assertions.JsonNodeAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;


public class ServiceResourceTest
{
    private static final ObjectReader mapper = Jackson.getReader();

    @Rule
    public JaxDaggerRule app = new JaxDaggerRule();

    @Test
    public void testPostService() throws Exception
    {
        HttpResponse r = Request.Post(app.resolve("/srv"))
                                .body(new JacksonEntity(ImmutableMap.of("url", "http://foo/",
                                                                        "type", "foo",
                                                                        "version", "1.2.3",
                                                                        "pool", "blue")))
                                .execute()
                                .returnResponse();

        assertThat(r.getStatusLine().getStatusCode()).isEqualTo(201);

        JsonNode root = mapper.readTree(r.getEntity().getContent());

        assertThat(root.at("/_links")).isObject();
        assertThat(root.at("/_links")).hasField("self");
        assertThat(root.at("/_links/self")).hasField("href");
        assertThat(root.at("/_links")).hasField("status");
        assertThat(root.at("/_links/status")).hasField("href");


        assertThat(root.at("/url")).textEquals("http://foo/");
        assertThat(root.at("/version")).textEquals("1.2.3");
        assertThat(root.at("/type")).textEquals("foo");
        assertThat(root.at("/pool")).textEquals("blue");
    }

    @Test
    public void testFollowSelfLinkOnPostedService() throws Exception
    {
        HttpResponse post_response = Request.Post(app.resolve("/srv"))
                                            .body(new JacksonEntity(ImmutableMap.of("url", "http://bar/",
                                                                                    "type", "foo",
                                                                                    "version", "1.2.3",
                                                                                    "pool", "blue")))
                                            .execute()
                                            .returnResponse();

        JsonNode post_root = mapper.readTree(post_response.getEntity().getContent());
        URI self_url = app.getBaseUri().resolve(post_root.at("/_links/self/href").textValue());

        HttpResponse get_response = Request.Get(self_url).execute().returnResponse();
        JsonNode get_root = mapper.readTree(get_response.getEntity().getContent());

        assertThat(get_root).isEqualTo(post_root);
    }

    @Test
    public void testQueryAll() throws Exception
    {
        createSrv(URI.create("http://foo-1.snc1/bar"), "foo", Version.valueOf("1.2.3"), "general");
        createSrv(URI.create("http://foo-2.snc1/bar"), "foo", Version.valueOf("1.3.0+2014.03.26"), "general");

        JsonNode root = Json.GET(app.resolve("/srv"));

        assertThat(root.at("/services")).isArray();
        assertThat(root.at("/services")).hasSize(2);
        assertThat(root.at("/services/0/type")).textEquals("foo");
        assertThat(root.at("/services/1/type")).textEquals("foo");
    }

    @Test
    public void testQueryVersionFilter() throws Exception
    {
        createSrv(URI.create("http://foo-1.snc1/bar"), "foo", Version.valueOf("1.2.3"), "general");
        createSrv(URI.create("http://foo-2.snc1/bar"), "foo", Version.valueOf("1.3.0+2014.03.26"), "general");

        JsonNode root = Json.GET(app.resolve("/srv?version=1.3"));

        assertThat(root.at("/services")).isArray();
        assertThat(root.at("/services")).hasSize(1);
        assertThat(root.at("/services/0/type")).textEquals("foo");
    }

    @Test
    public void testDeltaUri() throws Exception
    {
        createSrv(URI.create("http://foo-1.snc1/bar"), "foo", Version.valueOf("1.2.3"), "general");
        createSrv(URI.create("http://foo-2.snc1/bar"), "foo", Version.valueOf("1.3.0+2014.03.26"), "general");

        JsonNode root = Json.GET(app.resolve("/srv"));
        assertThat(root.at("/_links")).hasField("delta");
        assertThat(root.at("/_links/delta")).hasField("href");
    }


    private void createSrv(URI url, String type, Version version, String pool) throws IOException
    {
        Json.POST(app.resolve("srv"), ImmutableMap.of("url", url,
                                                      "type", type,
                                                      "version", version.toString(),
                                                      "pool", pool));
    }
}
