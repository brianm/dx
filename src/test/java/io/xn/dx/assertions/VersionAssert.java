package io.xn.dx.assertions;

import io.xn.dx.reps.Version;
import org.assertj.core.api.AbstractAssert;

public class VersionAssert extends AbstractAssert<VersionAssert, Version>
{
    protected VersionAssert(final Version actual)
    {
        super(actual, VersionAssert.class);
    }

    public static VersionAssert assertThat(Version actual) {
        return new VersionAssert(actual);
    }

    public VersionAssert satisfies(String other) {
        if (!actual.satisfies(Version.valueOf(other))) {
            failWithMessage("Version %s does not satisfy %s", actual, other);
        }
        return this;
    }

    public VersionAssert doesNotSatisfy(final String other)
    {
        if (actual.satisfies(Version.valueOf(other))) {
            failWithMessage("Version %s should not satisfy %s", actual, other);
        }
        return this;
    }
}
