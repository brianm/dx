package io.xn.dx.vendor;

import com.github.zafarkhaja.semver.Version;
import org.junit.Test;

import static io.xn.dx.assertions.GdxAssertions.assertThat;

public class JacksonTest
{
    @Test
    public void testWriteVersion() throws Exception
    {
        Version v = Version.valueOf("1.2.3");
        String val = Jackson.getWriter().writeValueAsString(v);
        assertThat(val).isEqualTo("\"1.2.3\"");
    }

    @Test
    public void testReadVersion() throws Exception
    {
        Version version = Jackson.getMapper().readValue("\"1.2.3\"", Version.class);
        assertThat(version).isEqualTo(Version.valueOf("1.2.3"));
    }
}
