package io.xn.dx.storage;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.airlift.units.Duration;
import io.xn.dx.reps.Service;
import io.xn.dx.reps.Status;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class InMemoryStorage implements Storage
{
    private final AtomicLong ids = new AtomicLong(0);
    private final ConcurrentMap<String, Service> data = Maps.newConcurrentMap();
    private final Map<String, ScheduledFuture<?>> expiries = Maps.newConcurrentMap();

    private final ScheduledExecutorService cron;

    public InMemoryStorage(ScheduledExecutorService cron)
    {
        this.cron = cron;
    }

    @Override
    public Service create(URI heartbeatBaseUri, final Service d)
    {
        String id = Long.toString(ids.getAndIncrement());
        final Service stored = d.withId(id).withHeartBeatBaseUri(heartbeatBaseUri);
        data.put(id, stored);
        if (stored.getTtl().isPresent()) {
            ScheduledFuture<?> f = cron.schedule(() -> {
                expiries.remove(id);
                data.computeIfPresent(id, (k, v) -> v.withStatus(Status.expired));
            }, stored.getTtl().get().toMillis(), TimeUnit.MILLISECONDS);
            expiries.put(id, f);
        }
        return stored;
    }

    @Override
    public Optional<Service> lookup(final String id)
    {
        return Optional.fromNullable(data.get(id));
    }

    @Override
    public Set<Service> query(Map<String, String> filters)
    {
        Set<Service> candidates = Sets.newLinkedHashSet(data.values());
        return ServiceQuery.filter(filters, candidates);
    }

    @Override
    public Optional<Service> updateStatus(final String id, final Status status)
    {
        Service s = data.get(id);
        if (s == null) {
            return Optional.absent();
        }
        return Optional.of(data.compute(id, (k, v) -> v.withStatus(status)));
    }

    @Override
    public Optional<Duration> heartbeat(final String id, final Duration ttl)
    {
        ScheduledFuture<?> f = expiries.get(id);
        if (f == null) {
            return Optional.absent();
        }

        f.cancel(false);
        Service old = data.computeIfPresent(id, (k, v) -> v.withTtl(ttl));
        if (old == null) {
            return Optional.absent();
        }

        ScheduledFuture<?> new_f = cron.schedule(() -> {
            expiries.remove(id);
            data.computeIfPresent(id, (k, v) -> v.withStatus(Status.expired));
        }, ttl.toMillis(), TimeUnit.MILLISECONDS);
        expiries.put(id, new_f);

        return Optional.of(ttl);
    }
}
