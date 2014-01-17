package io.xn.dx.storage;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import io.xn.dx.Descriptor;

import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

public class InMemoryStorage implements Storage
{
    private final ConcurrentMap<String, Descriptor> data = Maps.newConcurrentMap();

    @Override
    public Descriptor create(final Descriptor d)
    {
        if (d.getInstanceId().isPresent()) {
            data.put(d.getInstanceId().get(), d);
            return d;
        }
        else {
            String id = UUID.randomUUID().toString();
            Descriptor stored  = d.withId(id);
            data.put(id, stored);
            return stored;
        }
    }

    @Override
    public Optional<Descriptor> lookup(final String id)
    {
        return Optional.fromNullable(data.get(id));
    }
}
