package fr.petitsplats.dao;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Transactional
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
        return entityManager.merge(entity);
    }

    @Override
    public void evict(Object entity) {
        ((Session) entityManager.getDelegate()).evict(entity);
    }

    @Override
    public <T> T getEntity(Class<T> entityClass, Serializable id) {
        T entity = getEntityManager().find(entityClass, id);
        if (entity == null) {
            throw new EntityNotFoundException();
        }
        return entity;
    }

    @SuppressWarnings("unchecked")
    protected <T> T getEntityByNaturalId(Class<T> entityClass,
            String naturalIdName, Object naturalIdValue) {
        Criteria criteria = ((Session) entityManager.getDelegate())
                .createCriteria(entityClass);
        criteria.add(Restrictions.naturalId()
                .set(naturalIdName, naturalIdValue));
        return (T) criteria.uniqueResult();
    }

}
