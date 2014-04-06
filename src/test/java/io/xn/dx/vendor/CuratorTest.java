package io.xn.dx.vendor;

import com.google.common.base.Charsets;
import org.apache.curator.framework.CuratorFramework;
import org.junit.Rule;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CuratorTest
{
    @Rule
    public EmbeddedZooKeeperRule zk = new EmbeddedZooKeeperRule();

    @Test
    public void testFoo() throws Exception
    {
        CuratorFramework cf = zk.getCurator();

        cf.create()
          .creatingParentsIfNeeded()
          .forPath("/hello", "world".getBytes(Charsets.UTF_8));

        byte[] bytes = cf.getData().forPath("/hello");

        assertThat(bytes).isEqualTo("world".getBytes());
    }
}
