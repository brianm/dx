package io.xn.dx.storage;

import com.google.common.base.Optional;
import io.xn.dx.reps.Service;

import java.util.Map;
import java.util.Set;

public class ZooKeeperStorage implements Storage
{
    public ZooKeeperStorage() {

    }

    @Override
    public Service create(final Service d)
    {
        return null;
    }

    @Override
    public Optional<Service> lookup(final String id)
    {
        return null;
    }

    @Override
    public Set<Service> query(final Map<String, String> filters)
    {
        return null;
    }
}
