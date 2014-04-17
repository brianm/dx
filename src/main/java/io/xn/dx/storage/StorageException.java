package io.xn.dx.storage;

public class StorageException extends RuntimeException
{
    public StorageException(final String msg, final Exception cause)
    {
        super(msg, cause);
    }
}
