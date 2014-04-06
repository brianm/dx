package io.xn.dx.storage;

import javax.ws.rs.WebApplicationException;

public class StorageException extends WebApplicationException
{
    public StorageException(String message)
    {
        super(message, 500);
    }

    public StorageException(final String msg, final Exception cause)
    {
        super(msg, cause, 500);
    }
}
