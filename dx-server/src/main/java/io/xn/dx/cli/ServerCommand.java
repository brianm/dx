package io.xn.dx.cli;


import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import io.airlift.command.Command;
import io.airlift.command.Option;
import io.undertow.Undertow;
import io.xn.dx.jaxrs.DaggerApplication;
import io.xn.dx.jaxrs.DaggerApplicationDefaults;
import io.xn.dx.server.DxServerModule;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import javax.inject.Singleton;

@Module(includes = {DxServerModule.class, DaggerApplicationDefaults.class},
        injects = DaggerApplication.class)
@Command(name = "server",
         description = "Run the dx server")
public class ServerCommand implements Runnable
{
    private static final Logger log = LoggerFactory.getLogger(ServerCommand.class);

    @Option(name = {"-H", "--host"}, title = "bindHost", description = "Host to bind server to")
    public String bindHost = "0.0.0.0";

    @Option(name = {"-p", "--port"}, title = "bindPort", description = "Port to bind server to")
    public int bindPort = 7070;

    @Override
    public void run()
    {
        UndertowJaxrsServer ut = new UndertowJaxrsServer();

        DaggerApplication app = ObjectGraph.create(this).get(DaggerApplication.class);
        ut.deploy(app);
        ut.start(Undertow.builder().addListener(app.getBindPort(), app.getBindHost()));

        try {
            Thread.currentThread().join();
        }
        catch (InterruptedException e) {
            log.info("shutting down");
            ut.stop();
        }
    }

    @Provides
    @Named("bind-port")
    @Singleton
    public Integer getPort()
    {
        return bindPort;
    }

    @Provides
    @Named("bind-host")
    String getBindHost()
    {
        return bindHost;
    }
}
