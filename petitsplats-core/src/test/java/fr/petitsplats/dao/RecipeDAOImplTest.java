package fr.petitsplats.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Iterator;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolationException;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import fr.petitsplats.domain.Ingredient;
import fr.petitsplats.domain.Recipe;
import fr.petitsplats.domain.RecipePicture;
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
    private byte[] image;

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

        cuisson = RecipeStep.builder().withLabel("faire cuire les pates")
                .withOrder(2).build();
        pret = RecipeStep.builder().withLabel("c'est pret").withOrder(5)
                .build();
        couper = RecipeStep.builder().withLabel("couper le pain").withOrder(1)
                .build();

        recipe = new Recipe();
        recipe.setTitle("title");
        recipe.addIngredient(jambon);
        recipe.addRecipeStep(cuisson);
        recipe.addRecipeStep(pret);

        InputStream in = getClass().getResourceAsStream("/google.png");
        image = IOUtils.toByteArray(in);

    }

    @Test
    public void testSaveAndLoad() throws Exception {

        Assert.assertNull(recipe.getId());
        recipeDAO.save(recipe);
        Assert.assertTrue(recipe.getId() > 0);
        flushSession();

        Recipe searchedRecipe = recipeDAO.getEntity(Recipe.class,
                recipe.getId());
        assertEquals(recipe.getId(), searchedRecipe.getId());
        assertEquals(jambon.getLabel(), searchedRecipe.getIngredients()
                .iterator().next().getLabel());
        assertEquals(cuisson.getLabel(), searchedRecipe.getRecipeSteps()
                .first().getLabel());
        assertEquals(pret.getLabel(), searchedRecipe.getRecipeSteps().last()
                .getLabel());

        Integer order = -1;
        for (Iterator<RecipeStep> iterator = searchedRecipe.getRecipeSteps()
                .iterator(); iterator.hasNext();) {
            RecipeStep rs = iterator.next();
            assertTrue(rs.getOrder() > order);
            order = rs.getOrder();
        }

    }

    @Test
    public void testCreate2recipesWithSameIngredients() throws Exception {

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
        Iterator<Ingredient> it = recipe.getIngredients().iterator();
        recipe2.addIngredient(it.next());
        recipe2.addIngredient(it.next());
        recipe2.addRecipeStep(couper);
        recipeDAO.save(recipe2);
        assertEquals(2, recipe2.getIngredients().size());
        flushSession();

        // asserts
        Recipe searchedRecipe2 = recipeDAO.getEntity(Recipe.class,
                recipe2.getId());
        assertEquals(2, searchedRecipe2.getIngredients().size());
        Iterator<Ingredient> itRecipe1 = recipe.getIngredients().iterator();

        Ingredient i1 = itRecipe1.next();
        Ingredient i2 = itRecipe1.next();

        Assert.assertTrue(searchedRecipe2.getIngredients().contains(i1));
        Assert.assertTrue(searchedRecipe2.getIngredients().contains(i2));

    }

    @Test
    public void testCreate2recipesWithSameIngredientsButDetached()
            throws Exception {

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
        Iterator<Ingredient> it = recipe.getIngredients().iterator();

        Ingredient i1 = new Ingredient();
        i1.setId(it.next().getId());
        recipe2.addIngredient(i1);

        Ingredient i2 = new Ingredient();
        i2.setId(it.next().getId());
        recipe2.addIngredient(i2);

        assertFalse(i1.getId().equals(i2.getId()));

        recipe2.addRecipeStep(couper);
        recipeDAO.save(recipe2);
        assertEquals(2, recipe2.getIngredients().size());
        flushSession();

        // asserts
        Recipe searchedRecipe2 = recipeDAO.getEntity(Recipe.class,
                recipe2.getId());
        Iterator<Ingredient> itRecipe1 = recipe.getIngredients().iterator();
        i1 = itRecipe1.next();
        i2 = itRecipe1.next();

        Assert.assertTrue(searchedRecipe2.getIngredients().contains(i1));
        Assert.assertTrue(searchedRecipe2.getIngredients().contains(i2));

    }

    @Test
    public void testLoadNonExistingRecipe() throws Exception {

        Recipe searchedRecipe = recipeDAO.getEntity(Recipe.class, -1);
        assertNull(searchedRecipe);
    }

    @Test
    public void testSaveAndLoadImage() throws Exception {
        RecipePicture rp = new RecipePicture();
        rp.setImage(image);
        rp.setId(1);
        recipeDAO.save(rp);
        flushSession();

        RecipePicture searchedRecipePicture = recipeDAO.getEntity(
                RecipePicture.class, 1);
        assertEquals(Integer.valueOf(1), searchedRecipePicture.getId());
        assertEquals(image.length, searchedRecipePicture.getImage().length);

    }

    @Test
    public void testSaveAndUpdateRecipeWithNewIngredient() throws Exception {
        Assert.assertNull(recipe.getId());
        recipeDAO.save(recipe);
        Assert.assertTrue(recipe.getId() > 0);
        Assert.assertEquals(1, recipe.getIngredients().size());
        flushSession();

        Ingredient pates = new Ingredient();
        pates.setLabel("pates");

        Recipe searchedRecipe = recipeDAO.getEntity(Recipe.class,
                recipe.getId());
        entityManager.clear();
        searchedRecipe.getIngredients().add(pates);
        recipeDAO.merge(searchedRecipe);
        flushSession();

        recipe = recipeDAO.getEntity(Recipe.class, searchedRecipe.getId());
        Assert.assertEquals(2, recipe.getIngredients().size());
    }

    @Ignore
    public void testUpdateRecipeWithDetachedIngredients() throws Exception {
        Assert.assertNull(recipe.getId());
        recipeDAO.save(recipe);
        Assert.assertTrue(recipe.getId() > 0);
        flushSession();

        Ingredient pates = new Ingredient();
        pates.setLabel("pates");

        Recipe searchedRecipe = recipeDAO.getEntity(Recipe.class,
                recipe.getId());
        entityManager.clear();
        searchedRecipe.getIngredients().add(pates);
        recipeDAO.merge(searchedRecipe);
        flushSession();

        recipe = recipeDAO.getEntity(Recipe.class, searchedRecipe.getId());
        Assert.assertEquals(2, recipe.getIngredients().size());
    }
}
