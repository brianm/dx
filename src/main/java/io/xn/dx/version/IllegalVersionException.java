package io.xn.dx.version;

import static java.lang.String.format;

public class IllegalVersionException extends RuntimeException
{
    public IllegalVersionException(final String version)
    {
        super(format("'%s' is an illegal version, see http://semver.org/spec/v2.0.0.html", version));
    }
}
