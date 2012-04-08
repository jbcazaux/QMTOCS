package fr.petitsplats.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class RecipeDAOImpl extends AbstractDAO implements RecipeDAO {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void toto() {
        logger.debug("coucou");

    }

}
