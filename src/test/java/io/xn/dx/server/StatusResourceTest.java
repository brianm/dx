package io.xn.dx.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import io.xn.dx.reps.Service;
import io.xn.dx.reps.Status;
import io.xn.dx.vendor.DxServerRule;
import io.xn.dx.version.Version;
import org.junit.Rule;
import org.junit.Test;

import java.net.URI;
import java.util.Optional;

import static io.xn.dx.assertions.JsonNodeAssert.assertThat;

public class StatusResourceTest
{
    @Rule
    public DxServerRule app = new DxServerRule();

    @Test
    public void testStatusRoundTrips() throws Exception
    {
        Service one = app.createService(URI.create("http://foo"),
                                        "foo",
                                        Version.valueOf("1.2.3"),
                                        "blue",
                                        Optional.of(Status.ok));

        URI status_uri = one.getLinks().get("status").getHref();
        JsonNode node = app.GET(status_uri);
        assertThat(node.at("/status")).textEquals("ok");
    }

    @Test
    public void statusDefaultsToUnavailable() throws Exception
    {
        Service one = app.createService(URI.create("http://foo"),
                                        "foo",
                                        Version.valueOf("1.2.3"),
                                        "blue",
                                        Optional.empty());

        URI status_uri = one.getLinks().get("status").getHref();
        JsonNode node = app.GET(status_uri);
        assertThat(node.at("/status")).textEquals("unavailable");
    }

    @Test
    public void testUpdateStatus() throws Exception
    {
        Service one = app.createService(URI.create("http://foo"),
                                        "foo",
                                        Version.valueOf("1.2.3"),
                                        "blue",
                                        Optional.of(Status.ok));

        URI status_uri = one.getLinks().get("status").getHref();
        JsonNode node = app.POST(status_uri, ImmutableMap.of("status", "unavailable"));
        assertThat(node.at("/status")).textEquals("unavailable");
    }
}
