package io.xn.dx.storage;

import com.github.zafarkhaja.semver.Version;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import io.xn.dx.reps.Service;
import io.xn.dx.reps.Status;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class BaseStorageTest
{
    private Storage storage;
    private Service foo;
    private Service bar;

    protected abstract Storage createStorage();

    protected abstract void releaseStorage(Storage s);


    @Before
    public void setUp() throws Exception
    {
        this.storage = createStorage();
        Service foo = new Service(URI.create("http://foo:8989/"),
                                  "general",
                                  Version.valueOf("1.1.2"),
                                  "foo",
                                  Optional.of(Status.ok));
        Service bar = new Service(URI.create("http://bar:8989/"),
                                  "general",
                                  Version.valueOf("1.0.1"),
                                  "bar",
                                  Optional.of(Status.unavailable));
        this.foo = storage.create(foo);
        this.bar = storage.create(bar);
    }

    @After
    public void tearDown() throws Exception
    {
        this.releaseStorage(storage);
        storage = null;
    }

    @Test
    public void testStoringAddsLinks() throws Exception
    {
        assertThat(foo.getLinks()).containsKey("self");
        assertThat(foo.getLinks()).containsKey("status");
    }

    @Test
    public void testQueryNoFilters() throws Exception
    {
        Set<Service> all = storage.query(Collections.<String, String>emptyMap());
        assertThat(all).containsOnly(foo, bar);
    }

    @Test
    public void testFilterOnType() throws Exception
    {
        Set<Service> foos = storage.query(ImmutableMap.of("type", "foo"));
        assertThat(foos).containsOnly(foo);
    }

    @Test
    public void testFilterOnStatus() throws Exception
    {
        Set<Service> foos = storage.query(ImmutableMap.of("status", "ok"));
        assertThat(foos).containsOnly(foo);
    }

    @Test
    public void testFilterOnStatusAndType() throws Exception
    {
        Set<Service> rs = storage.query(ImmutableMap.of("status", "ok",
                                                        "type", "bar"));
        assertThat(rs).isEmpty();
    }

    @Test
    public void testFilterOnPool() throws Exception
    {
        Set<Service> rs = storage.query(ImmutableMap.of("pool", "general"));
        assertThat(rs).containsOnly(foo, bar);
    }

    @Test
    public void testFilterVersionExact() throws Exception
    {
        Set<Service> rs = storage.query(ImmutableMap.of("version", "1.1.2"));
        assertThat(rs).containsOnly(foo);
    }

    @Test
    public void testFilterVersionMinor() throws Exception
    {
        Set<Service> rs = storage.query(ImmutableMap.of("version", "1.1"));
        assertThat(rs).containsOnly(foo);
    }
}