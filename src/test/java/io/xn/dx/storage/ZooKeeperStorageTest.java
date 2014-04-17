package io.xn.dx.storage;

import io.xn.dx.vendor.EmbeddedZooKeeperRule;
import io.xn.dx.vendor.Jackson;
import org.junit.Before;
import org.junit.Rule;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assume.assumeThat;

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

    @Override
    protected boolean isHeartbeatImplemented()
    {
        return false;
    }

    @Override
    protected boolean isDeltaImplemented()
    {
        return false;
    }
}
