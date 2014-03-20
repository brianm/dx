package io.xn.dx.ext;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.collect.Maps;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class JacksonTest
{
    private ObjectMapper mapper;
    private String yaml;

    @Before
    public void setUp() throws Exception
    {
        mapper = new ObjectMapper(new YAMLFactory());
        yaml = "---\n" +
               "server:\n" +
               "  host: 0.0.0.0\n" +
               "  port: 7070\n" +
               "foo.rb:\n" +
               "  path: /tmp/\n" +
               "bar.jar:\n" +
               "  url: http://dx.xn.io/\n";
    }

    @Test
    public void testCaptureRandomTopLevelObjects() throws Exception
    {
        ConfigTree config = mapper.readValue(yaml, ConfigTree.class);

        assertThat(config.plugins.keySet()).containsExactly("foo.rb", "bar.jar");
        assertThat(config.server).isEqualToComparingFieldByField(new Server()
        {{
                this.host = "0.0.0.0";
                this.port = 7070;
            }});
    }

    @Test
    public void testRegenerateYaml() throws Exception
    {
        ConfigTree config = mapper.readValue(yaml, ConfigTree.class);
        JsonNode node = config.plugins.get("foo.rb");
        String foo = mapper.writeValueAsString(node);
        assertThat(foo).isEqualTo("---\npath: \"/tmp/\"\n");
    }


    public static class Server
    {
        public String host;
        public Integer port;
        public JsonNode child;
    }

    public static class ConfigTree
    {
        public Server server;
        public Map<String, JsonNode> plugins = Maps.newLinkedHashMap();

        @JsonAnySetter
        public void plugin(String name, JsonNode node)
        {
            plugins.put(name, node);
        }
    }

}
