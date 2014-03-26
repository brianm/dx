package io.xn.dx.storage;

public class InMemoryStorageTest extends BaseStorageTest
{
    @Override
    protected Storage createStorage()
    {
        return new InMemoryStorage();
    }

    @Override
    protected void releaseStorage(final Storage s)
    {
        // NOOP
    }
}
