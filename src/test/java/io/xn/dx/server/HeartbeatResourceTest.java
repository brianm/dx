package io.xn.dx.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import io.airlift.units.Duration;
import io.xn.dx.reps.Service;
import io.xn.dx.reps.Status;
import io.xn.dx.reps.Version;
import io.xn.dx.vendor.DxServerRule;
import org.junit.Rule;
import org.junit.Test;

import java.net.URI;
import java.util.Optional;

import static io.xn.dx.assertions.JsonNodeAssert.assertThat;

public class HeartbeatResourceTest
{
    @Rule
    public DxServerRule app = new DxServerRule();

    @Test
    public void testUpdateTtl() throws Exception
    {
//        Service one = app.createService(URI.create("http://foo"),
//                                        "foo",
//                                        Version.valueOf("1.2.3"),
//                                        "blue",
//                                        Duration.valueOf("100ms"));
//
//        URI status_uri = one.getLinks().get("heartbeat").getHref();
//        JsonNode node = app.POST(status_uri, ImmutableMap.of("ttl", "200ms"));
//        assertThat(node.at("/ttl")).isTextual();
    }

}
