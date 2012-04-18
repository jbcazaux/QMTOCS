package fr.petitsplats.dao;

import static org.junit.Assert.assertNull;

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
@ContextConfiguration(locations = { "classpath:/petitsplats-core.xml",
        "classpath:/datasource-test.xml" })
@Transactional
public class RecipeDAOImplTest {

    @Autowired
    private RecipeDAO recipeDAO;

    @PersistenceContext
    protected EntityManager entityManager;

    private Recipe recipe;
    private Ingredient jambon;
    private RecipeStep cuisson;
    private RecipeStep pret;
    private RecipeStep couper;

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
        jambon.setLabel("jambon");

        cuisson = new RecipeStep("faire cuire les pates");
        pret = new RecipeStep("c'est pret");
        couper = new RecipeStep("couper le pain");

        recipe = new Recipe();
        recipe.setTitle("title");
        recipe.setImageId(12);
        recipe.addIngredient(jambon);
        recipe.addRecipeStep(cuisson);
        recipe.addRecipeStep(pret);
    }

    @Test
    public void testSaveAndLoad() throws Exception {

        jambon.setLabel("jambon");
        recipe.addIngredient(jambon);
        Assert.assertNull(recipe.getId());
        recipeDAO.save(recipe);
        Assert.assertEquals(Integer.valueOf(1), recipe.getId());
        flushSession();

        Recipe searchedRecipe = recipeDAO.getEntity(Recipe.class, 1);
        Assert.assertEquals(Integer.valueOf(1), searchedRecipe.getId());
        Assert.assertEquals(jambon.getLabel(), searchedRecipe.getIngredients()
                .get(0).getLabel());
        Assert.assertEquals(cuisson.getLabel(), searchedRecipe.getRecipeSteps()
                .first().getLabel());
        Assert.assertEquals(pret.getLabel(), searchedRecipe.getRecipeSteps()
                .last().getLabel());

    }

    @Test
    public void testSave2recipesWithSameIngredients() throws Exception {

        // recette1
        Ingredient pain = new Ingredient();
        pain.setLabel("pain");
        recipe.addIngredient(pain);
        Assert.assertNull(recipe.getId());
        recipeDAO.save(recipe);
        flushSession();

        // recette2
        Recipe recipe2 = new Recipe();
        recipe2.setTitle("title2");
        recipe2.setImageId(13);
        recipe2.addIngredient(recipe.getIngredients().get(0));
        recipe2.addIngredient(recipe.getIngredients().get(1));
        recipe2.addRecipeStep(couper);
        recipeDAO.save(recipe2);
        flushSession();

        Recipe searchedRecipe2 = recipeDAO.getEntity(Recipe.class,
                recipe2.getId());
        Assert.assertEquals(recipe.getIngredients().get(0).getId(),
                searchedRecipe2.getIngredients().get(0).getId());

        Assert.assertEquals(recipe.getIngredients().get(1).getId(),
                searchedRecipe2.getIngredients().get(1).getId());

    }

    @Test
    public void testLoadNonExistingRecipe() throws Exception {

        Recipe searchedRecipe = recipeDAO.getEntity(Recipe.class, -1);
        assertNull(searchedRecipe);

    }

}
