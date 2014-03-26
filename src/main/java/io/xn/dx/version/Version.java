package io.xn.dx.version;

import com.google.common.base.Function;
import com.google.common.base.Optional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Version
{
    public static Pattern VERSION_PATTERN = Pattern.compile("(\\d+)              #major \n" +
                                                            "(?:\\.(\\d+))?      #minor \n" +
                                                            "(?:\\.(\\d+))?      #patch \n" +
                                                            "(?:-([\\w\\.]+))?   #prerelease \n" +
                                                            "(?:\\+([\\w\\.]+))? #buildnumber",
                                                            Pattern.COMMENTS);
    private final int major;
    private final Optional<Integer> minor;
    private final Optional<Integer> patch;
    private final Optional<String> pre;
    private final Optional<String> build;

    public Version(final int major,
                   final Optional<Integer> minor,
                   final Optional<Integer> patch,
                   final Optional<String> pre,
                   final Optional<String> build)
    {


        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.pre = pre;
        this.build = build;
    }

    public int getMajor()
    {
        return major;
    }

    public Optional<Integer> getMinor()
    {
        return minor;
    }

    public Optional<Integer> getPatch()
    {
        return patch;
    }

    public Optional<String> getPre()
    {
        return pre;
    }

    public Optional<String> getBuild()
    {
        return build;
    }

    public static Version valueOf(final String value)
    {
        Matcher m = VERSION_PATTERN.matcher(value);
        if (!m.matches()) {
            throw new IllegalVersionException(value);
        }
        int major = Integer.parseInt(m.group(1));
        Optional<Integer> minor = Optional.fromNullable(m.group(2)).transform(ATOI);
        Optional<Integer> patch = Optional.fromNullable(m.group(3)).transform(ATOI);
        Optional<String> pre = Optional.fromNullable(m.group(4));
        Optional<String> build = Optional.fromNullable(m.group(5));

        return new Version(major, minor, patch, pre, build);
    }

    private static Function<String, Integer> ATOI = new Function<String, Integer>()
    {
        @Override
        public Integer apply(final String input)
        {
            return Integer.parseInt(input);
        }
    };
}
