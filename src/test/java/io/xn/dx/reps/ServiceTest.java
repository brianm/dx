package io.xn.dx.reps;

import com.fasterxml.jackson.databind.JsonNode;
import io.xn.dx.version.Version;
import com.google.common.base.Optional;
import io.xn.dx.vendor.Jackson;
import org.junit.Test;

import java.net.URI;

import static io.xn.dx.assertions.JsonNodeAssert.assertThat;

public class ServiceTest
{
    @Test
    public void testJsonShape() throws Exception
    {
        Service s = new Service(URI.create("memcached://cache7.snv1:11211"),
                                "general",
                                Version.valueOf("1.2.3"),
                                "foo",
                                Optional.of(Status.ok)).withId("123");
        String json = Jackson.getWriter().writeValueAsString(s);
        JsonNode root = Jackson.getReader().readTree(json);

        assertThat(root).isObject();
        assertThat(root.at("/url")).textEquals("memcached://cache7.snv1:11211");
        assertThat(root.at("/pool")).textEquals("general");
        assertThat(root.at("/type")).textEquals("foo");
        assertThat(root.at("/version")).textEquals("1.2.3");
        assertThat(root.at("/status")).textEquals("ok");

        assertThat(root.at("/_links")).isObject();
        assertThat(root.at("/_links/self")).isObject();
        assertThat(root.at("/_links/self/href")).textEquals("/srv/123");
        assertThat(root.at("/_links/status")).isObject();
        assertThat(root.at("/_links/status/href")).textEquals("/srv/123/status");

    }
}
