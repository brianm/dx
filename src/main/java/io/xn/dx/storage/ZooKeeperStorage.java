package io.xn.dx.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import io.airlift.units.Duration;
import io.xn.dx.reps.Service;
import io.xn.dx.reps.Status;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.KeeperException;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ZooKeeperStorage implements Storage
{
    private final CuratorFramework curator;
    private final ObjectMapper mapper;

    public ZooKeeperStorage(CuratorFramework curator, ObjectMapper mapper)
    {
        this.curator = curator;
        this.mapper = mapper;
    }

    @Override
    public Service create(URI heartbeatBaseUri, final Service d)
    {
        UUID id = UUID.randomUUID();
        Service s = d.withId(id.toString());
        byte[] data = jsonify(s);
        store(id, data);
        if (s.getTtl().isPresent()) {
            monitor(s);
        }
        return s;
    }

    private void store(final UUID id, final byte[] data)
    {
        try
        {
            curator.create().forPath("/" + id.toString(), data);
        }
        catch (Exception e)
        {
            throw new StorageException(String.format("Unable to store %s", id), e);
        }
    }

    private byte[] jsonify(final Service s)
    {
        try
        {
            return mapper.writeValueAsBytes(s);
        }
        catch (JsonProcessingException e)
        {
            throw new StorageException("cannot marshall service to json", e);
        }
    }

    @Override
    public Optional<Service> lookup(final String id)
    {
        try
        {
            byte[] data = curator.getData().forPath("/" + id);
            return Optional.of(mapper.readValue(data, Service.class));
        }
        catch (KeeperException.NoNodeException e) {
            return Optional.absent();
        }
        catch (Exception e)
        {
            throw new StorageException(String.format("Unable to lookup %s", id), e);
        }
    }

    @Override
    public void delete(final String id) throws StorageException
    {
        try
        {
            curator.delete().forPath("/" + id);
        }
        catch (Exception e)
        {
            throw new StorageException("unable to delete " + id, e);
        }
    }

    @Override
    public Set<Service> query(final Map<String, String> filters)
    {
        try
        {
            Set<Service> candidates = Sets.newLinkedHashSet();
            List<String> children = curator.getChildren().forPath("");
            for (String child : children) {
                candidates.add(lookup(child).get());
            }
            return ServiceQuery.filter(filters, candidates);
        }
        catch (Exception e)
        {
            throw new StorageException("unable to execute query", e);
        }
    }

    @Override
    public Optional<Service> updateStatus(final String id, final Status status)
    {
        Optional<Service> s =  lookup(id);
        if (!s.isPresent()) {
            return s;
        }
        Service old = s.get();
        Service ns = old.withStatus(status);
        try
        {
            curator.setData().forPath("/" + id, mapper.writeValueAsBytes(ns));
        }
        catch (Exception e)
        {
            throw new StorageException("unable to serialize service", e);
        }
        return Optional.of(ns);
    }

    private void monitor(final Service s)
    {
//        throw new UnsupportedOperationException("Not Yet Implemented!");
    }

    @Override
    public Optional<Duration> heartbeat(final String id, final Duration ttl)
    {
        throw new UnsupportedOperationException("Not Yet Implemented!");
    }
}
