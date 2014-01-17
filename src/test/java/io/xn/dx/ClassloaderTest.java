package io.xn.dx;

import io.xn.dx.cli.Main;
import org.junit.Test;

import java.net.URL;
import java.net.URLClassLoader;

import static org.assertj.core.api.Assertions.assertThat;

public class ClassloaderTest
{
    @Test
    public void testFoo() throws Exception
    {
        URLClassLoader cl = new URLClassLoader(new URL[] {}, null);
        Class string = cl.loadClass("java.lang.String");
        assertThat(string).isEqualTo(String.class);
    }

    @Test(expected = ClassNotFoundException.class)
    public void testBar() throws Exception
    {
        URLClassLoader cl = new URLClassLoader(new URL[] {}, null);
        cl.loadClass(Main.class.getName());
    }
}
