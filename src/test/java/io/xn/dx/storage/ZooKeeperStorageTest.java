package io.xn.dx.storage;

import io.xn.dx.vendor.EmbeddedZooKeeperRule;
import io.xn.dx.vendor.Jackson;
import org.junit.Rule;

public class ZooKeeperStorageTest extends BaseStorageTest
{
    @Rule
    public EmbeddedZooKeeperRule zk = new EmbeddedZooKeeperRule();

    @Override
    protected Storage createStorage()
    {
        return new ZooKeeperStorage(zk.getCurator(), Jackson.getMapper());
    }

    @Override
    protected void releaseStorage(final Storage s)
    {
        // NOOP
    }
}
