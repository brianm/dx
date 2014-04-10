package io.xn.dx.reps;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.airlift.units.Duration;
import io.xn.dx.vendor.Jackson;
import io.xn.dx.version.Version;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

public class ServiceSetTest
{

    private ServiceSet ss;

    @Before
    public void setUp() throws Exception
    {
        ss = ServiceSet.build(ImmutableMap.of("type", "memcached"),
                              "17",
                              ImmutableList.of(new Service(Optional.of("0"),
                                                           URI.create("memcached://hello:11211"),
                                                           "general",
                                                           Version.valueOf("1.2.3"),
                                                           "memcached",
                                                           Optional.<Status>absent(),
                                                           Optional.<Duration>absent()),
                                               new Service(Optional.of("1"),
                                                           URI.create("memcached://world:11211"),
                                                           "general",
                                                           Version.valueOf("1.2.3"),
                                                           "memcached",
                                                           Optional.<Status>absent(),
                                                           Optional.<Duration>absent())
                              )
        );
    }

    @Test
    public void testSelfUriCorrect() throws Exception
    {
        assertThat(ss.getLinks()).containsKey("self");
        Link delta = ss.getLinks().get("self");
        assertThat(delta.getHref().getQuery()).contains("type=memcached");
    }

    @Test
    public void testDeltaUriCorrect() throws Exception
    {
        assertThat(ss.getLinks()).containsKey("delta");
        Link delta = ss.getLinks().get("delta");
        assertThat(delta.getHref().getQuery()).contains("delta=17");
    }

    @Test
    public void testJsonStructure() throws Exception
    {
        String json = Jackson.getWriter().writeValueAsString(ss);
        JsonNode root = Jackson.getReader().readTree(json);

        assertThat(root.at("/services").isArray()).isTrue();
        assertThat(root.at("/services").size()).isEqualTo(2);

        assertThat(root.at("/services/0/type").textValue()).isEqualTo("memcached");
        assertThat(root.at("/services/1/type").textValue()).isEqualTo("memcached");

        assertThat(root.at("/_links/self/href").textValue()).isEqualTo("/srv?type=memcached");
        assertThat(root.at("/_links/delta/href").textValue()).isEqualTo("/srv?type=memcached&delta=17");
    }
}
