package fr.petitsplats.dao;

import java.io.Serializable;

public interface DataAccessObject {

    <T> T save(T entity);

    <T> T getEntity(Class<T> entityClass, Serializable id);

    <T> T merge(final T entity);
}
