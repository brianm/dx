package io.xn.dx.server;

import com.google.common.collect.ImmutableMap;
import io.xn.dx.jaxrs.JaxDaggerRule;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.ClassRule;
import org.junit.Test;
import retrofit.http.Body;
import retrofit.http.EncodedPath;
import retrofit.http.GET;
import retrofit.http.POST;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ServiceResourceTest
{

    @ClassRule
    public static JaxDaggerRule app = new JaxDaggerRule();

    @Test
    public void testPostService() throws Exception
    {
        SrvClient client = app.retrofit(SrvClient.class);

        Srv srv = client.post(ImmutableMap.of("url", "http://foo/",
                                              "type", "foo",
                                              "version", "1.2.3",
                                              "pool", "blue"));

        assertThat(srv.url).isEqualTo("http://foo/");
        assertThat(srv.pool).isEqualTo("blue");
        assertThat(srv.version).isEqualTo("1.2.3");
        assertThat(srv.type).isEqualTo("foo");

        assertThat(srv._links).containsKey("self");
        assertThat(srv._links).containsKey("status");
        assertThat(srv._links.get("self")).containsKey("href");
        assertThat(srv._links.get("status")).containsKey("href");


    }

    @Test
    public void testFollowSelfLinkOnPostedService() throws Exception
    {
        SrvClient client = app.retrofit(SrvClient.class);

        Srv srv = client.post(ImmutableMap.of("url", "http://foo2/",
                                              "type", "foo",
                                              "version", "1.2.3",
                                              "pool", "blue"));

        Srv srv2 = client.getSrv(srv._links.get("self").get("href"));
        assertThat(EqualsBuilder.reflectionEquals(srv, srv2)).isTrue();
    }

    public static interface SrvClient
    {
        @POST("/srv")
        public Srv post(@Body Map<String, String> srv);

        @GET("/{path}")
        public Srv getSrv(@EncodedPath("path") String path);
    }

    public static class Srv
    {
        public Map<String, Map<String, String>> _links;
        public String url;
        public String type;
        public String pool;
        public String version;
    }
}
