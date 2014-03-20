package io.xn.dx;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;


public class NetUtil
{
    public static synchronized int findUnusedPort()
    {
        try (ServerSocket socket = new ServerSocket())
        {
            socket.bind(new InetSocketAddress(0));
            return socket.getLocalPort();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Unable to bind random high port", e);
        }
    }
}
