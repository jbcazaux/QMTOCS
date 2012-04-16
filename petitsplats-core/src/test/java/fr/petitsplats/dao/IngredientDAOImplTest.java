package fr.petitsplats.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolationException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import fr.petitsplats.domain.Ingredient;
import fr.petitsplats.domain.Recipe;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/petitsplats-core.xml",
        "classpath:/datasource-test.xml" })
@Transactional
public class IngredientDAOImplTest {

    @Autowired
    private IngredientDAO ingredientDAO;

    @PersistenceContext
    protected EntityManager entityManager;

    private Ingredient jambon;

    @Before
    public void setUp() throws Exception {
        Recipe r = new Recipe();

        jambon = new Ingredient();
        jambon.setName("jambon");
        jambon.setRecipes(Collections.singletonList(r));

    }

    @Test
    public void saveAndLoad() throws Exception {
        ingredientDAO.save(jambon);
        flushSession();

        assertTrue(jambon.getId() > 0);
        ingredientDAO.getEntity(Ingredient.class, jambon.getId());
        assertFalse(CollectionUtils.isEmpty(jambon.getRecipes()));
        assertNull("recipe must not be persisted", jambon.getRecipes().get(0)
                .getId());

    }

    @Test
    public void testFindAll() throws Exception {
        ingredientDAO.save(jambon);
        flushSession();

        List<Ingredient> allIngredients = ingredientDAO.findAll();
        assertFalse(CollectionUtils.isEmpty(allIngredients));
    }

    protected void flushSession() throws Exception {
        try {
            entityManager.flush();
        } catch (ConstraintViolationException e) {
            throw new Exception(e.getConstraintViolations().toString(), e);
        }
    }
}
