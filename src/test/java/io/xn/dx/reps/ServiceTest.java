package io.xn.dx.reps;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Optional;
import io.airlift.units.Duration;
import io.xn.dx.vendor.Jackson;
import io.xn.dx.version.Version;
import org.junit.Test;

import java.net.URI;

import static io.xn.dx.assertions.JsonNodeAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;


public class ServiceTest
{
    @Test
    public void testJsonShapeWithId() throws Exception
    {
        Service s = new Service(Optional.of("123"),
                                URI.create("memcached://cache7.snv1:11211"),
                                "general",
                                Version.valueOf("1.2.3"),
                                "foo",
                                Optional.of(Status.ok),
                                Optional.of(Duration.valueOf("100ms")));

        String json = Jackson.getWriter().writeValueAsString(s);
        JsonNode root = Jackson.getReader().readTree(json);

        assertThat(root).isObject();
        assertThat(root.at("/url")).textEquals("memcached://cache7.snv1:11211");
        assertThat(root.at("/pool")).textEquals("general");
        assertThat(root.at("/type")).textEquals("foo");
        assertThat(root.at("/version")).textEquals("1.2.3");
        assertThat(root.at("/status")).textEquals("ok");
        assertThat(root.at("/id")).textEquals("123");

        assertThat(root.at("/_links")).isObject();
        assertThat(root.at("/_links/self")).isObject();
        assertThat(root.at("/_links/self/href")).textEquals("/srv/123");
        assertThat(root.at("/_links/status")).isObject();
        assertThat(root.at("/_links/status/href")).textEquals("/srv/123/status");
        assertThat(root.at("/_links/heartbeat")).isObject();
        assertThat(root.at("/_links/heartbeat/href")).textEquals("/srv/123/heartbeat");
    }

    @Test
    public void testJsonShapeNoId() throws Exception
    {
        Service s = new Service(Optional.<String>absent(),
                                URI.create("memcached://cache7.snv1:11211"),
                                "general",
                                Version.valueOf("1.2.3"),
                                "foo",
                                Optional.of(Status.ok),
                                Optional.<Duration>absent());
        String json = Jackson.getWriter().writeValueAsString(s);
        JsonNode root = Jackson.getReader().readTree(json);

        assertThat(root).doesNotHaveField("id");
    }

    @Test
    public void testWithHeartbeatResolvery() throws Exception
    {
        Service s = new Service(Optional.of("123"),
                                URI.create("memcached://cache7.snv1:11211"),
                                "general",
                                Version.valueOf("1.2.3"),
                                "foo",
                                Optional.of(Status.ok),
                                Optional.of(Duration.valueOf("100ms")));
        Service s2 = s.withHeartBeatBaseUri(URI.create("http://localhost/waffles"));

        assertThat(s2.getLinks()).containsKey("heartbeat");
        assertThat(s2.getLinks().get("heartbeat").getHref()).isEqualTo(URI.create("http://localhost/srv/123/heartbeat"));
    }

    @Test
    public void testRoundTripNoId() throws Exception
    {
        Service s = new Service(Optional.<String>absent(),
                                URI.create("memcached://cache7.snv1:11211"),
                                "general",
                                Version.valueOf("1.2.3"),
                                "foo",
                                Optional.of(Status.ok),
                                Optional.<Duration>absent());
        String json = Jackson.getWriter().writeValueAsString(s);
        Service s2 = Jackson.getMapper().readValue(json, Service.class);

        assertThat(s2).isEqualTo(s);
    }

    @Test
    public void testRoundTripWithId() throws Exception
    {
        Service s = new Service(Optional.of("123"),
                                URI.create("memcached://cache7.snv1:11211"),
                                "general",
                                Version.valueOf("1.2.3"),
                                "foo",
                                Optional.of(Status.ok),
                                Optional.<Duration>absent());
        String json = Jackson.getWriter().writeValueAsString(s);
        Service s2 = Jackson.getMapper().readValue(json, Service.class);

        assertThat(s2).isEqualTo(s);
    }
}
