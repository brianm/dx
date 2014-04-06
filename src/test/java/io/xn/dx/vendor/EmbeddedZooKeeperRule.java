package io.xn.dx.vendor;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.BoundedExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.junit.rules.ExternalResource;

import java.io.IOException;

public class EmbeddedZooKeeperRule extends ExternalResource
{
    private TestingServer ts;
    private CuratorFramework curator;

    public CuratorFramework getCurator()
    {
        return curator;
    }

    @Override
    protected void before() throws Throwable
    {
        ts = new TestingServer();
        curator = CuratorFrameworkFactory.builder()
                                         .namespace("ezkr")
                                         .connectString(ts.getConnectString())
                                         .retryPolicy(new BoundedExponentialBackoffRetry(10, 100, 7))
                                         .build();
        curator.getConnectionStateListenable().addListener(new ConnectionStateListener()
        {
            @Override
            public void stateChanged(final CuratorFramework client, final ConnectionState newState)
            {

            }
        });
        curator.start();
    }

    @Override
    protected void after()
    {
        curator.close();
        try
        {
            ts.close();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
