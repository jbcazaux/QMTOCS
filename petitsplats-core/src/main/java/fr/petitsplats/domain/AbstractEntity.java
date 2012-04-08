package fr.petitsplats.domain;

import java.io.Serializable;

public abstract class AbstractEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();
}
