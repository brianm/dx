package io.xn.dx.storage;

import com.google.common.base.Optional;
import io.airlift.units.Duration;
import io.xn.dx.reps.Service;
import io.xn.dx.reps.Status;
import io.xn.dx.server.HeartbeatResource;

import java.net.URI;
import java.util.Map;
import java.util.Set;

public interface Storage
{
    Service create(URI heartbeatBaseUri, Service d) throws StorageException;

    Optional<Service> lookup(String id) throws StorageException;

    Set<Service> query(Map<String, String> filters) throws StorageException;

    Optional<Service> updateStatus(String id, Status status);

    Optional<Duration> heartbeat(String id, Duration ttl);
}
