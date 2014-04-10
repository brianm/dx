package io.xn.dx.storage;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import io.airlift.units.Duration;
import io.xn.dx.reps.Service;
import io.xn.dx.reps.Status;
import io.xn.dx.reps.Version;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.guava.api.Assertions.assertThat;

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
        Service foo = new Service(Optional.<String>absent(),
                                  URI.create("http://foo:8989/"),
                                  "general",
                                  Version.valueOf("1.1.2"),
                                  "foo",
                                  Optional.of(Status.ok),
                                  Optional.<Duration>absent());
        Service bar = new Service(Optional.<String>absent(),
                                  URI.create("http://bar:8989/"),
                                  "general",
                                  Version.valueOf("1.0.1"),
                                  "bar",
                                  Optional.of(Status.unknown),
                                  Optional.<Duration>absent());
        this.foo = storage.create(URI.create("/"), foo);
        this.bar = storage.create(URI.create("/"), bar);
    }

    @After
    public void tearDown() throws Exception
    {
        this.releaseStorage(storage);
        storage = null;
    }

    @Test
    public void testRead() throws Exception
    {
        Optional<Service> foo2 = storage.lookup(foo.getId().get());
        assertThat(foo2).isPresent();
        assertThat(foo2).contains(foo);
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

    @Test
    public void testUpdateStatus() throws Exception
    {
        Service f1 = storage.lookup(foo.getId().get()).get();
        assertThat(f1.getStatus()).isEqualTo(Status.ok);

        Optional<Service> updated = storage.updateStatus(foo.getId().get(), Status.valueOf("green"));
        assertThat(updated).isPresent();
        assertThat(updated.get().getStatus()).isEqualTo(Status.valueOf("green"));

        Service f2 = storage.lookup(foo.getId().get()).get();
        assertThat(f2.getStatus()).isEqualTo(Status.valueOf("green"));


    }
}
