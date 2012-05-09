package fr.petitsplats.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolationException;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
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
                .withOrder(1).build();
        pret = RecipeStep.builder().withLabel("c'est pret").withOrder(2)
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
        recipe2.addRecipeStep(RecipeStep.builder().withLabel("etape1")
                .withOrder(1).build());
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
        recipe2.addRecipeStep(RecipeStep.builder().withLabel("etape1")
                .withOrder(1).build());
        Iterator<Ingredient> it = recipe.getIngredients().iterator();

        // les ingredients détachés
        Ingredient i1 = new Ingredient();
        i1.setId(it.next().getId());
        recipe2.addIngredient(i1);

        Ingredient i2 = new Ingredient();
        i2.setId(it.next().getId());
        recipe2.addIngredient(i2);

        assertFalse(i1.getId().equals(i2.getId()));
        assertFalse(i1.equals(i2));

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
    public void testSaveAndUpdateDetachedRecipeWithNewIngredient()
            throws Exception {
        // sauve recette1
        Assert.assertNull(recipe.getId());
        recipeDAO.save(recipe);
        Assert.assertTrue(recipe.getId() > 0);
        Assert.assertEquals(1, recipe.getIngredients().size());
        flushSession();

        Ingredient pates = new Ingredient();
        pates.setLabel("pates");

        Recipe searchedRecipe = recipeDAO.getEntity(Recipe.class,
                recipe.getId());
        // clean la session pour detacher les objets
        entityManager.clear();
        detachRecipe(searchedRecipe);
        searchedRecipe.getIngredients().add(pates);
        recipeDAO.merge(searchedRecipe);
        flushSession();

        recipe = recipeDAO.getEntity(Recipe.class, searchedRecipe.getId());
        Assert.assertEquals(2, recipe.getIngredients().size());
    }

    @Test
    public void testUpdateDetachedRecipeWithNewDetachedIngredients()
            throws Exception {
        // enregistre la recette
        Assert.assertNull(recipe.getId());
        recipeDAO.save(recipe);
        Assert.assertTrue(recipe.getId() > 0);
        flushSession();

        Recipe searchedRecipe = recipeDAO.getEntity(Recipe.class,
                recipe.getId());
        // clean de la session pour detacher les objets
        entityManager.clear();
        detachRecipe(searchedRecipe);
        // ajoute un nouvel ingredient (sans id)
        Ingredient pates = new Ingredient();
        pates.setLabel("pates");
        searchedRecipe.getIngredients().add(pates);
        recipeDAO.merge(searchedRecipe);
        flushSession();

        recipe = recipeDAO.getEntity(Recipe.class, searchedRecipe.getId());
        Assert.assertEquals(2, recipe.getIngredients().size());
    }

    @Test
    public void testUpdateDetachedRecipeWithExistingDetachedIngredients()
            throws Exception {
        // enregistre la recette
        Assert.assertNull(recipe.getId());
        recipeDAO.save(recipe);
        Assert.assertTrue(recipe.getId() > 0);
        // cree un nouvel ingredient en base
        Ingredient salade = new Ingredient();
        salade.setLabel("salade");
        entityManager.persist(salade);
        flushSession();

        // load la recette a modifier
        Recipe searchedRecipe = recipeDAO.getEntity(Recipe.class,
                recipe.getId());
        // clean de la session et de la recette
        entityManager.clear();
        detachRecipe(searchedRecipe);
        // ajoute un nouvel ingredient (avec id)
        searchedRecipe.getIngredients().add(salade);

        recipeDAO.merge(searchedRecipe);
        flushSession();

        recipe = recipeDAO.getEntity(Recipe.class, searchedRecipe.getId());
        Assert.assertEquals(2, recipe.getIngredients().size());
    }

    @Test
    public void testUpdateRecipeByDeletingDetachedIngredients()
            throws Exception {
        // enregistre la recette avec 2 ingredients (salade et jambon)
        Assert.assertNull(recipe.getId());
        Ingredient salade = new Ingredient();
        salade.setLabel("salade");
        recipe.addIngredient(salade);
        recipeDAO.save(recipe);
        Assert.assertTrue(recipe.getId() > 0);
        // cree un nouvel ingredient en base
        flushSession();

        // load la recette a modifier
        Recipe searchedRecipe = recipeDAO.getEntity(Recipe.class,
                recipe.getId());
        // clean de la session pour detacher la recette
        entityManager.clear();
        detachRecipe(searchedRecipe);
        searchedRecipe.getIngredients().remove(salade);
        recipeDAO.merge(searchedRecipe);
        flushSession();

        recipe = recipeDAO.getEntity(Recipe.class, searchedRecipe.getId());
        Assert.assertEquals(1, recipe.getIngredients().size());
    }

    @Test
    public void testUpdateRecipeWithNewFirstStep() throws Exception {
        // enregistre la recette
        Assert.assertNull(recipe.getId());
        recipeDAO.save(recipe);
        Assert.assertTrue(recipe.getId() > 0);

        // load la recette a modifier
        Recipe searchedRecipe = recipeDAO.getEntity(Recipe.class,
                recipe.getId());
        // clean de la session pour detacher la recette
        entityManager.clear();
        detachRecipe(searchedRecipe);
        // changement de l ordre des etapes
        searchedRecipe.getRecipeSteps().clear();
        cuisson.setOrder(2);
        pret.setOrder(3);
        RecipeStep saler = RecipeStep.builder().withLabel("saler").withOrder(1)
                .build();
        searchedRecipe.addRecipeStep(saler);
        searchedRecipe.addRecipeStep(cuisson);
        searchedRecipe.addRecipeStep(pret);

        // save and asserts
        recipeDAO.merge(searchedRecipe);
        flushSession();

        recipe = recipeDAO.getEntity(Recipe.class, searchedRecipe.getId());
        Assert.assertEquals(3, recipe.getRecipeSteps().size());

    }

    @Test
    public void testGetLastRecipe() throws Exception {

        // recette1
        recipeDAO.save(recipe);
        flushSession();

        // recette2
        Recipe recipe2 = new Recipe();
        recipe2.setTitle("title2");
        recipe2.addIngredient(jambon);
        recipe2.addRecipeStep(RecipeStep.builder().withLabel("step")
                .withOrder(1).build());

        recipeDAO.save(recipe2);
        flushSession();

        // asserts
        assertEquals(recipe2.getId(), recipeDAO.getLastRecipe().getId());

    }

    private void detachRecipe(Recipe r) {
        Set<Ingredient> ingredients = new HashSet<Ingredient>();
        ingredients.addAll(r.getIngredients());
        r.setIngredients(ingredients);

        SortedSet<RecipeStep> recipeSteps = new TreeSet<RecipeStep>();
        recipeSteps.addAll(r.getRecipeSteps());
        r.setRecipeSteps(recipeSteps);
    }

}
