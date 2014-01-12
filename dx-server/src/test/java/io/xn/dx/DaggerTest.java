package io.xn.dx;

import org.junit.Test;

public class DaggerTest
{
    @Test
    public void testFoo() throws Exception
    {

    }
//    @Test
//    @Ignore
//    public void testFoo() throws Exception
//    {
//        ObjectGraph graph = ObjectGraph.create(new MyMod());
//        Bar bar = graph.get(Bar.class);
//        Bar bar2 = graph.get(Bar.class);
//        assertThat(bar).isSameAs(bar2);
//
//    }
//
//    @Module
//    public static class MyMod
//    {
//        @Provides
//        @Singleton
//        Bar bar(Foo foo)
//        {
//            return new Bar(foo);
//        }
//    }
//
//    public static class Bar
//    {
//        public Bar(Foo foo)
//        {
//
//        }
//    }
//
//    public static class Foo
//    {
//        @Inject
//        public Foo() {}
//    }
}
