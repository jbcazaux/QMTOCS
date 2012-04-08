package fr.petitsplats.dao;

import java.io.Serializable;

public interface DataAccessObject {

    <T> T save(T entity);

    <T> T merge(T entity);

    void evict(Object entity);

    <T> T getEntity(Class<T> entityClass, Serializable id);

}
