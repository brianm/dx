package io.xn.dx.reps;

import com.fasterxml.jackson.databind.JsonNode;
import io.xn.dx.vendor.Jackson;
import org.junit.Test;

import java.net.URI;

import static io.xn.dx.assertions.JsonNodeAssert.assertThat;

public class HalContainerTest
{
    @Test
    public void testFoo() throws Exception
    {
        HalContainer container = new HalContainer(new Foo("Brian")).addLink("bar", URI.create("/srv"));
        String json = Jackson.getWriter().writeValueAsString(container);

        JsonNode root = Jackson.getMapper().readTree(json);
        assertThat(root).hasField("name");
        assertThat(root.at("/name")).textEquals("Brian");
        assertThat(root).hasField("_links");
        assertThat(root.at("/_links")).isObject();
        assertThat(root.at("/_links")).hasField("bar");
    }

    public static class Foo
    {
        public String name;

        public Foo(String name) {
            this.name = name;
        }
    }
}
