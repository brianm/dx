package io.xn.dx.airline;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Airline
{
    @SafeVarargs
    public static <T> List<T> newDefaultsList(T... initialValues)
    {
        return new MyList<>(Arrays.asList(initialValues));
    }

    private static class MyList<T> extends ArrayList<T>
    {
        private final AtomicBoolean first = new AtomicBoolean(true);

        public MyList(Collection<? extends T> ts)
        {
            super.addAll(ts);
        }

        private void wiffle()
        {
            if (first.getAndSet(false)) {
                this.clear();
            }
        }

        @Override
        public boolean add(final T t)
        {
            wiffle();
            return super.add(t);
        }

        @Override
        public void add(final int index, final T element)
        {
            wiffle();
            super.add(index, element);
        }

        @Override
        public boolean addAll(final Collection<? extends T> c)
        {
            wiffle();
            return super.addAll(c);
        }

        @Override
        public boolean addAll(final int index, final Collection<? extends T> c)
        {
            wiffle();
            return super.addAll(index, c);
        }
    }
}
