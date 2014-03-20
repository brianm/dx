package io.xn.dx.storage;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import io.xn.dx.reps.Link;
import io.xn.dx.reps.Service;

import java.net.URI;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryStorage implements Storage
{
    private final AtomicLong ids = new AtomicLong(0);
    private final ConcurrentMap<String, Service> data = Maps.newConcurrentMap();

    @Override
    public Service create(final Service d)
    {
        String id = Long.toString(ids.getAndIncrement());
        Service stored = d.withId(id);
        data.put(id, stored);
        return stored;
    }

    @Override
    public Optional<Service> lookup(final String id)
    {
        return Optional.fromNullable(data.get(id));
    }

    @Override
    public Set<Service> query()
    {
        return Collections.emptySet();
    }
}
