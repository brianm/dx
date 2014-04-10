package io.xn.dx.server;

import io.xn.dx.storage.Storage;

public class HeartbeatResource
{
    private final Storage storage;
    private final String id;

    public HeartbeatResource(final Storage storage, final String id)
    {
        this.storage = storage;
        this.id = id;
    }
}
