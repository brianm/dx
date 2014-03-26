package io.xn.dx.version;

import org.junit.Test;

import java.util.regex.Matcher;

import static io.xn.dx.assertions.GdxAssertions.assertThat;


public class VersionTest
{
    @Test
    public void testThreeNumsMatches() throws Exception
    {
        Matcher m = Version.VERSION_PATTERN.matcher("1.2.3");
        assertThat(m.matches()).isTrue();
        assertThat(m.group(1)).isEqualTo("1");
        assertThat(m.group(2)).isEqualTo("2");
        assertThat(m.group(3)).isEqualTo("3");
        assertThat(m.group(4)).isNull();
        assertThat(m.group(5)).isNull();
    }

    @Test
    public void testTwoNumsMatches() throws Exception
    {
        Matcher m = Version.VERSION_PATTERN.matcher("1.2");
        assertThat(m.matches()).isTrue();
        assertThat(m.group(1)).isEqualTo("1");
        assertThat(m.group(2)).isEqualTo("2");
        assertThat(m.group(3)).isNull();
        assertThat(m.group(4)).isNull();
        assertThat(m.group(5)).isNull();

        assertThat(m.matches()).isTrue();
    }

    @Test
    public void testOneNumMatches() throws Exception
    {
        Matcher m = Version.VERSION_PATTERN.matcher("1");
        assertThat(m.matches()).isTrue();
    }

    @Test
    public void testPrereleaseInfoMatches() throws Exception
    {
        Matcher m = Version.VERSION_PATTERN.matcher("1-foo.3");
        assertThat(m.matches()).isTrue();
    }

    @Test
    public void testBuildInfoMatches() throws Exception
    {
        Matcher m = Version.VERSION_PATTERN.matcher("1.2.3-foo.3+12.12");
        assertThat(m.matches()).isTrue();
    }

    @Test
    public void testVersionFromTheThree() throws Exception
    {
        Version v = Version.valueOf("1.2.3");
        assertThat(v.getMajor()).isEqualTo(1);
        assertThat(v.getMinor()).contains(2);
        assertThat(v.getPatch()).contains(3);
        assertThat(v.getPre()).isAbsent();
        assertThat(v.getBuild()).isAbsent();

    }
}
