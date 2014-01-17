package io.xn.dx.storage;

import com.google.common.base.Optional;
import io.xn.dx.Descriptor;

public interface Storage
{
    Descriptor create(Descriptor d);

    Optional<Descriptor> lookup(String id);
}
