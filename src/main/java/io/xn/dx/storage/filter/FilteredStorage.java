package io.xn.dx.storage.filter;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import io.airlift.units.Duration;
import io.xn.dx.reps.Service;
import io.xn.dx.reps.Status;
import io.xn.dx.storage.Storage;
import io.xn.dx.storage.StorageException;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class FilteredStorage implements Storage
{
    private final Storage storage;
    private final List<Function<Service, Service>> filters = Lists.newCopyOnWriteArrayList();

    public FilteredStorage(Storage storage)
    {
        this.storage = storage;
    }

    @Override
    public Service create(final URI heartbeatBaseUri, final Service d) throws StorageException
    {
        return storage.create(heartbeatBaseUri, d);
    }

    @Override
    public Optional<Service> lookup(final String id) throws StorageException
    {
        Optional<Service> rs = storage.lookup(id);
        if (rs.isPresent())
        {
            Service svc = rs.get();
            for (Function<Service, Service> filter : filters) {
                svc = filter.apply(svc);
                if (svc == null) {
                    return Optional.absent();
                }
            }
            return Optional.of(svc);
        }
        return rs;
    }

    @Override
    public void delete(final String id) throws StorageException
    {
        storage.delete(id);
    }

    @Override
    public Set<Service> query(final Map<String, String> filters) throws StorageException
    {
        return storage.query(filters);
    }

    @Override
    public Optional<Service> updateStatus(final String id, final Status status)
    {
        return storage.updateStatus(id, status);
    }

    @Override
    public Optional<Duration> heartbeat(final String id, final Duration ttl)
    {
        return storage.heartbeat(id, ttl);
    }

    public String addFilter(final String filter) throws Exception
    {
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("nashorn");
        ScriptObjectMirror it = (ScriptObjectMirror) engine.eval(filter);
        filters.add((svc) -> (Service)it.call(it, svc));
        return "hello";
    }
}
