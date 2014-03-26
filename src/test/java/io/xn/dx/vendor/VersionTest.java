package io.xn.dx.vendor;

import com.github.zafarkhaja.semver.Version;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class VersionTest
{
    @Test
    public void testFoo() throws Exception
    {
        Version v = Version.valueOf("1.2");
        assertThat(v.satisfies("~1.2")).isTrue();
    }
}
