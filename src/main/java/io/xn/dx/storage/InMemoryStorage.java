package io.xn.dx.storage;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.xn.dx.reps.Service;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryStorage implements Storage
{
    private final AtomicLong ids = new AtomicLong(0);
    private final ConcurrentMap<String, Service> data = Maps.newConcurrentMap();

    @Override
    public Service create(URI _, final Service d)
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
    public Set<Service> query(Map<String, String> filters)
    {
        Set<Service> candidates = Sets.newLinkedHashSet(data.values());
        return ServiceQuery.filter(filters, candidates);
    }
}
