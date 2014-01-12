package io.xn.dx;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.Test;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

public class JettyTest
{
    @Test
    public void testEmbeddedExample() throws Exception
    {
        int port = AvailablePortFinder.getNextAvailable();

        final Servlet s = new HttpServlet()
        {
            @Override
            protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException
            {
                resp.getWriter().write("hello jetty!");
            }
        };

        Server server = new Server(port);
        ServletHandler handler = new ServletHandler();
        handler.addServletWithMapping(new ServletHolder(s), "/jetty");
        server.setHandler(handler);
        server.start();

        HttpClient http = new HttpClient();
        http.start();
        assertThat("hello jetty!").isEqualTo(http.GET(format("http://127.0.0.1:%d/jetty", port)).getContentAsString());
        http.stop();
        server.stop();
    }
}
