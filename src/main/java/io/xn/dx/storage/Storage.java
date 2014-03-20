package io.xn.dx.storage;

import com.google.common.base.Optional;
import io.xn.dx.reps.Service;

public interface Storage
{
    Service create(Service d);

    Optional<Service> lookup(String id);
}
