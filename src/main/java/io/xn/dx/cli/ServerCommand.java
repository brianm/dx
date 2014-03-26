package io.xn.dx.cli;


import com.google.common.base.Joiner;
import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import io.airlift.command.Arguments;
import io.airlift.command.Command;
import io.airlift.command.Option;
import io.undertow.Undertow;
import io.xn.dx.vendor.jaxrs.DaggerApplication;
import io.xn.dx.vendor.jaxrs.DaggerApplicationDefaults;
import io.xn.dx.server.DxServerModule;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;

@Module(includes = {DxServerModule.class, DaggerApplicationDefaults.class}, injects = DaggerApplication.class)
@Command(name = "server", description = "Run the groupon server")
public class ServerCommand implements Runnable
{
    private static final Logger log = LoggerFactory.getLogger(ServerCommand.class);

    @Option(name = {"-H", "--host"}, title = "bindHost", description = "Host to bind server to")
    public String bindHost = "0.0.0.0";

    @Option(name = {"-p", "--port"}, title = "bindPort", description = "Port to bind server to")
    public int bindPort = 7070;

    @Arguments
    public List<String> message = Airline.newDefaultsList("hello", "world");

    @Override
    public void run()
    {
        UndertowJaxrsServer ut = new UndertowJaxrsServer();
        DaggerApplication app = ObjectGraph.create(this).get(DaggerApplication.class);
        ut.deploy(app);
        ut.start(Undertow.builder().addListener(bindPort, bindHost));
        try {
            Thread.currentThread().join();
        }
        catch (InterruptedException e) {
            log.info("shutting down");
            ut.stop();
        }
    }

    @Provides
    @Singleton
    @Named("message")
    public String getMessage()
    {
        return Joiner.on(" ").join(message);
    }

    public static void main(String[] args)
    {
        Main.main(new String[]{"server", "this", "is", "a", "test"});
    }
}
