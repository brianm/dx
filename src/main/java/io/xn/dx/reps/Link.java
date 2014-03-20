package io.xn.dx.reps;

import java.net.URI;

public class Link
{
    private final URI href;

    public Link(final URI href)
    {
        this.href = href;
    }

    public URI getHref()
    {
        return href;
    }
}
