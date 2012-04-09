package fr.petitsplats.dao;

import java.util.Collections;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolationException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import fr.petitsplats.domain.Ingredient;
import fr.petitsplats.domain.Recipe;
import fr.petitsplats.domain.RecipeStep;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring-context.xml",
        "classpath:/datasource-test.xml" })
@Transactional
public class RecipeDAOTest {

    @Autowired
    private RecipeDAO recipeDAO;

    @PersistenceContext
    protected EntityManager entityManager;

    private Recipe recipe;
    private Ingredient jambon;
    private RecipeStep cuisson;
    private RecipeStep pret;

    protected void flushSession() throws Exception {
        try {
            entityManager.flush();
        } catch (ConstraintViolationException e) {
            throw new Exception(e.getConstraintViolations().toString(), e);
        }
    }

    @Before
    public void setUp() throws Exception {

        jambon = new Ingredient();
        jambon.setName("jambon");

        cuisson = new RecipeStep("faire cuire les pates");
        pret = new RecipeStep("c'est pret");

        entityManager.persist(jambon);
        flushSession();

        recipe = new Recipe();
        recipe.setTitle("title");
        recipe.setImageId(12);
        recipe.setIngredients(Collections.singletonList(jambon));
        recipe.addRecipeStep(cuisson);
        recipe.addRecipeStep(pret);
    }

    @Test
    public void testSaveAndLoad() throws Exception {
        Assert.assertNull(recipe.getId());
        recipeDAO.save(recipe);
        Assert.assertEquals(Integer.valueOf(1), recipe.getId());
        flushSession();

        Recipe searchedRecipe = recipeDAO.getEntity(Recipe.class, 1);
        Assert.assertEquals(Integer.valueOf(1), searchedRecipe.getId());
        Assert.assertEquals(jambon.getName(), searchedRecipe.getIngredients()
                .get(0).getName());
        Assert.assertEquals(cuisson.getLabel(), searchedRecipe.getRecipeSteps()
                .get(0).getLabel());
        Assert.assertEquals(pret.getLabel(), searchedRecipe.getRecipeSteps()
                .get(1).getLabel());

    }

}
