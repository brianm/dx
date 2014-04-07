package io.xn.dx.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import io.xn.dx.reps.Service;
import org.apache.curator.framework.CuratorFramework;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ZooKeeperStorage implements Storage
{
    private final CuratorFramework curator;
    private final ObjectMapper om;

    public ZooKeeperStorage(CuratorFramework curator, ObjectMapper om)
    {
        this.curator = curator;
        this.om = om;
    }

    @Override
    public Service create(URI _, final Service d)
    {
        UUID id = UUID.randomUUID();
        Service s = d.withId(id.toString());
        byte[] data = jsonify(s);
        store(id, data);
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
            return om.writeValueAsBytes(s);
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
            if (data == null) {
                return Optional.absent();
            }
            return Optional.of(om.readValue(data, Service.class));
        }
        catch (Exception e)
        {
            throw new StorageException(String.format("Unable to lookup %s", id), e);
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
}
