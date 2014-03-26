package io.xn.dx.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import io.xn.dx.ext.JacksonEntity;
import io.xn.dx.vendor.JaxDaggerRule;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.junit.ClassRule;
import org.junit.Test;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static io.xn.dx.assertions.JsonNodeAssert.assertThat;


public class ServiceResourceTest
{
    private static final ObjectMapper mapper = new ObjectMapper();

    @ClassRule
    public static JaxDaggerRule app = new JaxDaggerRule();

    @Test
    public void testPostService() throws Exception
    {
        HttpResponse r = Request.Post(app.getBaseUri().resolve("/srv"))
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
        HttpResponse post_response = Request.Post(app.getBaseUri().resolve("/srv"))
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
}
