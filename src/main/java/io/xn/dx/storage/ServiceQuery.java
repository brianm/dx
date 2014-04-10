package io.xn.dx.storage;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.xn.dx.reps.Service;
import io.xn.dx.reps.Status;
import io.xn.dx.reps.Version;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;

class ServiceQuery
{
    static Set<Service> filter(Map<String, String> filters, Set<Service> candidates)
    {
        List<Predicate<Service>> predicates = Lists.newArrayList();
        for (Map.Entry<String, String> entry : filters.entrySet()) {
            switch (entry.getKey()) {
                case "type":
                    predicates.add(new TypeFilter(entry.getValue()));
                    break;
                case "status":
                    predicates.add(new StatusFilter(entry.getValue()));
                    break;
                case "pool":
                    predicates.add(new PoolFilter(entry.getValue()));
                    break;
                case "version":
                    predicates.add(new VersionFilter(entry.getValue()));
                    break;
                default:
                    throw new UnsupportedOperationException(format("'%s' filters are not supported", entry.getKey()));
            }
        }
        return Sets.filter(candidates, Predicates.and(predicates));
    }

    private static class TypeFilter implements Predicate<Service>
    {
        private final String value;

        public TypeFilter(final String value) {this.value = value;}

        @Override
        public boolean apply(final Service input)
        {
            return value.equals(input.getType());
        }
    }

    private static class StatusFilter implements Predicate<Service>
    {
        private final Status value;

        public StatusFilter(final String value) {this.value = Status.valueOf(value);}

        @Override
        public boolean apply(final Service input)
        {
            return value.equals(input.getStatus());
        }
    }

    private static class PoolFilter implements Predicate<Service>
    {
        private final String value;

        public PoolFilter(final String value) {this.value = value;}

        @Override
        public boolean apply(final Service input)
        {
            return value.equals(input.getPool());
        }
    }

    private static class VersionFilter implements Predicate<Service>
    {
        private final Version value;

        public VersionFilter(final String value) {this.value = Version.valueOf(value);}

        @Override
        public boolean apply(final Service input)
        {
            return input.getVersion().satisfies(value);
        }
    }
}
