package fr.petitsplats.dao;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractDAO implements DataAccessObject {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @PersistenceContext
    private EntityManager entityManager;

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    protected Logger getLogger() {
        return logger;
    }

    @Override
    public <T> T save(final T entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Override
    public <T> T merge(final T entity) {
        entityManager.merge(entity);
        return entity;
    }

    @Override
    public <T> T getEntity(Class<T> entityClass, Serializable id) {
        T entity = getEntityManager().find(entityClass, id);
        if (entity == null) {
            StringBuilder sb = new StringBuilder("entity not found (class=");
            sb.append(entityClass);
            sb.append(", id=");
            sb.append(id);
            sb.append(')');
            getLogger().info(sb.toString());
        }
        return entity;
    }

    protected static <T> T initializeAndUnproxy(T var) {
        if (var == null) {
            throw new IllegalArgumentException("passed argument is null");
        }

        Hibernate.initialize(var);
        if (var instanceof HibernateProxy) {
            var = (T) ((HibernateProxy) var).getHibernateLazyInitializer()
                    .getImplementation();
        }
        return var;
    }
}
