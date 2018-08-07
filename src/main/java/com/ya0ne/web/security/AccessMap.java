package com.ya0ne.web.security;

import java.io.Serializable;

import com.ya0ne.core.domain.abstracts.Entity;

public interface AccessMap<T extends Entity<Long>> extends Serializable {
    String getIndirectReference(final T entity);
    T getDirectReference(final String indirectReference);
}
