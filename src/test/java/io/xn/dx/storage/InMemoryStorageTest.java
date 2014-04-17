package io.xn.dx.storage;

public class InMemoryStorageTest extends BaseStorageTest
{
    @Override
    protected Storage createStorage()
    {
        return new InMemoryStorage(this.getClockedExecutor() );
    }

    @Override
    protected void releaseStorage(final Storage s)
    {
        // NOOP
    }

    @Override
    protected boolean isHeartbeatImplemented()
    {
        return true;
    }

    @Override
    protected boolean isDeltaImplemented()
    {
        return false;
    }
}
