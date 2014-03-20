package io.xn.dx.storage;

import com.google.common.base.Optional;
import io.xn.dx.reps.Service;

import java.util.Set;

public interface Storage
{
    Service create(Service d);

    Optional<Service> lookup(String id);

    Set<Service> query();
}
