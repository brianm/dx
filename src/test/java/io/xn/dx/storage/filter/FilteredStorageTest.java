package io.xn.dx.storage.filter;

import com.google.common.base.Optional;
import io.airlift.units.Duration;
import io.xn.dx.reps.Service;
import io.xn.dx.reps.Status;
import io.xn.dx.reps.Version;
import io.xn.dx.storage.InMemoryStorage;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.guava.api.Assertions.assertThat;


public class FilteredStorageTest
{
    private InMemoryStorage storage;
    private FilteredStorage filtered;
    private ScheduledExecutorService cron;
    private Service foo;
    private Service bar;

    @Before
    public void setUp() throws Exception
    {
        this.cron = Executors.newScheduledThreadPool(1);
        this.storage = new InMemoryStorage(cron);
        this.filtered = new FilteredStorage(storage);
        Service foo = new Service(Optional.<String>absent(),
                                  URI.create("http://foo:8989/"),
                                  "general",
                                  Version.valueOf("1.1.2"),
                                  "foo",
                                  Optional.of(Status.ok),
                                  Optional.of(Duration.valueOf("10ms")));
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
        cron.shutdownNow();
    }

    @Test
    public void testFilterLookup() throws Exception
    {
        filtered.addFilter("function(svc) { return svc.withStatus('nashorned') }");

        Service svc = filtered.lookup(bar.getId()).get();
        assertThat(svc.getStatus()).isEqualTo(Status.valueOf("nashorned"));

        svc = storage.lookup(bar.getId()).get();
        assertThat(svc.getStatus()).isEqualTo(Status.valueOf("unknown"));
    }

    @Test
    public void testFilterLookupMakeGone() throws Exception
    {
        filtered.addFilter("function(svc) { if (svc.type == 'bar') { return null } else { return svc }}");
        assertThat(filtered.lookup(bar.getId())).isAbsent();
        assertThat(storage.lookup(bar.getId())).isPresent();
    }

}
