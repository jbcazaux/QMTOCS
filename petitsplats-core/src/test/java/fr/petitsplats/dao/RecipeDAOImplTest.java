package fr.petitsplats.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolationException;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.hibernate.proxy.HibernateProxy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import fr.petitsplats.domain.Ingredient;
import fr.petitsplats.domain.Recipe;
import fr.petitsplats.domain.RecipeIngredient;
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
    private RecipeIngredient _400gJambon;
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
        // entityManager.persist(jambon);

        _400gJambon = new RecipeIngredient();
        _400gJambon.setAmount("400g grammes");
        _400gJambon.setIngredient(jambon);

        cuisson = RecipeStep.builder().withLabel("faire cuire les pates")
                .withOrder(1).build();
        pret = RecipeStep.builder().withLabel("c'est pret").withOrder(2)
                .build();

        recipe = new Recipe();
        recipe.setTitle("title");
        recipe.addIngredient(_400gJambon);
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
        assertEquals(jambon.getLabel(), searchedRecipe.getRecipeIngredients()
                .iterator().next().getIngredient().getLabel());
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

        RecipeIngredient _100gPain = new RecipeIngredient(pain, "100g");
        recipe.addIngredient(_100gPain);

        Assert.assertNull(recipe.getId());
        recipeDAO.save(recipe);
        flushSession();

        // recette2
        Recipe recipe2 = new Recipe();
        recipe2.setTitle("title2");

        Iterator<RecipeIngredient> it = recipe.getRecipeIngredients()
                .iterator();
        RecipeIngredient ri1 = it.next();
        recipe2.addIngredient(new RecipeIngredient(ri1.getIngredient(), ri1
                .getAmount()));

        RecipeIngredient ri2 = it.next();
        recipe2.addIngredient(new RecipeIngredient(ri2.getIngredient(), ri2
                .getAmount()));

        recipe2.addRecipeStep(RecipeStep.builder().withLabel("etape1")
                .withOrder(1).build());
        recipeDAO.save(recipe2);
        assertEquals(2, recipe2.getRecipeIngredients().size());
        flushSession();

        // asserts
        Recipe searchedRecipe2 = recipeDAO.getEntity(Recipe.class,
                recipe2.getId());
        assertEquals(2, searchedRecipe2.getRecipeIngredients().size());
        Iterator<RecipeIngredient> itRecipe1 = recipe.getRecipeIngredients()
                .iterator();

        ri1 = itRecipe1.next();
        ri2 = itRecipe1.next();

        Iterator<RecipeIngredient> itRecipe2 = recipe2.getRecipeIngredients()
                .iterator();

        RecipeIngredient ri21 = itRecipe2.next();
        RecipeIngredient ri22 = itRecipe2.next();

        Assert.assertTrue(ri21.getIngredient().getId()
                .equals(ri1.getIngredient().getId())
                || ri21.getIngredient().getId()
                        .equals(ri2.getIngredient().getId()));
        Assert.assertTrue(ri22.getIngredient().getId()
                .equals(ri1.getIngredient().getId())
                || ri22.getIngredient().getId()
                        .equals(ri2.getIngredient().getId()));
        Assert.assertFalse(ri21.getIngredient().getId()
                .equals(ri22.getIngredient().getId()));

    }

    @Test
    public void testCreate2recipesWithSameIngredientsButDetached()
            throws Exception {

        // recette1
        Ingredient pain = new Ingredient();
        pain.setLabel("pain");

        RecipeIngredient _100gPain = new RecipeIngredient(pain, "100g");
        recipe.addIngredient(_100gPain);
        Assert.assertNull(recipe.getId());
        recipeDAO.save(recipe);
        flushSession();

        // recette2
        Recipe recipe2 = new Recipe();
        recipe2.setTitle("title2");
        recipe2.addRecipeStep(RecipeStep.builder().withLabel("etape1")
                .withOrder(1).build());
        Iterator<RecipeIngredient> it = recipe.getRecipeIngredients()
                .iterator();

        // les ingredients détachés
        Ingredient i1 = new Ingredient();
        i1.setId(it.next().getIngredient().getId());
        RecipeIngredient ri1 = new RecipeIngredient(i1, "200g");
        recipe2.addIngredient(ri1);

        Ingredient i2 = new Ingredient();
        i2.setId(it.next().getIngredient().getId());
        RecipeIngredient ri2 = new RecipeIngredient(i2, "300g");
        recipe2.addIngredient(ri2);

        assertFalse(i1.getId().equals(i2.getId()));
        assertFalse(i1.equals(i2));

        recipeDAO.save(recipe2);
        assertEquals(2, recipe2.getRecipeIngredients().size());
        flushSession();

        // asserts
        Recipe searchedRecipe2 = recipeDAO.getEntity(Recipe.class,
                recipe2.getId());
        Iterator<RecipeIngredient> itRecipe1 = recipe.getRecipeIngredients()
                .iterator();
        i1 = itRecipe1.next().getIngredient();
        i2 = itRecipe1.next().getIngredient();

        it = searchedRecipe2.getRecipeIngredients().iterator();
        boolean foundi1 = false;
        boolean foundi2 = false;
        while (it.hasNext()) {
            RecipeIngredient ri = it.next();
            if (ri.getIngredient().equals(i1)) {
                foundi1 = true;
            }
            if (ri.getIngredient().equals(i2)) {
                foundi2 = true;
            }
        }
        Assert.assertTrue(foundi1);
        Assert.assertTrue(foundi2);
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
        Assert.assertEquals(1, recipe.getRecipeIngredients().size());
        flushSession();

        Recipe searchedRecipe;// = recipeDAO.getEntity(Recipe.class,
        // recipe.getId());
        // clean la session pour detacher les objets
        entityManager.clear();
        Ingredient pates = new Ingredient();
        pates.setLabel("pates");

        RecipeIngredient _100gPates = new RecipeIngredient(pates, "200g");

        searchedRecipe = detachRecipe(recipe);
        searchedRecipe.addIngredient(_100gPates);
        recipeDAO.merge(searchedRecipe);
        flushSession();

        recipe = recipeDAO.getEntity(Recipe.class, searchedRecipe.getId());
        Assert.assertEquals(2, recipe.getRecipeIngredients().size());
    }

    @Test
    public void testUpdateDetachedRecipeWithNewDetachedIngredients()
            throws Exception {
        // enregistre la recette
        Assert.assertNull(recipe.getId());
        recipeDAO.save(recipe);
        Assert.assertTrue(recipe.getId() > 0);
        flushSession();

        // Recipe searchedRecipe = recipeDAO.getEntity(Recipe.class,
        // recipe.getId());
        // clean de la session pour detacher les objets
        entityManager.clear();
        Recipe searchedRecipe = detachRecipe(recipe);
        // ajoute un nouvel ingredient (sans id)
        Ingredient pates = new Ingredient();
        pates.setLabel("pates");
        RecipeIngredient _100gPates = new RecipeIngredient(pates, "100g");

        searchedRecipe.addIngredient(_100gPates);
        recipeDAO.merge(searchedRecipe);
        flushSession();

        recipe = recipeDAO.getEntity(Recipe.class, searchedRecipe.getId());
        Assert.assertEquals(2, recipe.getRecipeIngredients().size());
    }

    @Test
    public void testUpdateDetachedRecipeByAddingExistingDetachedIngredients()
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

        // clean de la session et de la recette
        entityManager.clear();
        Ingredient saladeDetached = new Ingredient();
        saladeDetached.setId(salade.getId());

        Recipe searchedRecipe = detachRecipe(recipe);
        // ajoute un nouvel ingredient (avec id)
        RecipeIngredient _1feuilleSalade = new RecipeIngredient(saladeDetached,
                "1 feuille");
        searchedRecipe.addIngredient(_1feuilleSalade);

        recipeDAO.merge(searchedRecipe);
        flushSession();

        recipe = recipeDAO.getEntity(Recipe.class, searchedRecipe.getId());
        Assert.assertEquals(2, recipe.getRecipeIngredients().size());
    }

    @Test
    public void testUpdateDetachedRecipeByReplacingIngredientWithAnExistingDetachedIngredients()
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

        // clean de la session et de la recette
        entityManager.clear();
        Ingredient saladeDetached = new Ingredient();
        saladeDetached.setId(salade.getId());

        Recipe searchedRecipe = detachRecipe(recipe);
        // ajoute un nouvel ingredient (avec id)
        RecipeIngredient ri = searchedRecipe.getRecipeIngredients().iterator()
                .next();
        ri.setIngredient(saladeDetached);

        recipeDAO.merge(searchedRecipe);
        flushSession();
        entityManager.clear();

        recipe = recipeDAO.getEntity(Recipe.class, searchedRecipe.getId());
        Assert.assertEquals(1, recipe.getRecipeIngredients().size());
        Assert.assertEquals(saladeDetached.getId(), recipe
                .getRecipeIngredients().iterator().next().getIngredient()
                .getId());
    }

    @Test
    public void testUpdateAmountOfDetachedIngredient() throws Exception {
        // sauve recette1
        Assert.assertNull(recipe.getId());
        recipeDAO.save(recipe);
        Assert.assertTrue(recipe.getId() > 0);
        Assert.assertEquals(1, recipe.getRecipeIngredients().size());
        flushSession();

        // clean la session pour detacher les objets
        entityManager.clear();

        Recipe searchedRecipe = detachRecipe(recipe);
        Iterator<RecipeIngredient> it = searchedRecipe.getRecipeIngredients()
                .iterator();
        RecipeIngredient ri1 = it.next();
        ri1.setAmount("new amount");

        recipeDAO.merge(searchedRecipe);
        flushSession();
        entityManager.clear();

        recipe = recipeDAO.getEntity(Recipe.class, searchedRecipe.getId());
        Assert.assertEquals(1, recipe.getRecipeIngredients().size());
        Assert.assertEquals("new amount", recipe.getRecipeIngredients()
                .iterator().next().getAmount());
    }

    @Test
    public void testUpdateRecipeByDeletingDetachedIngredients()
            throws Exception {
        // enregistre la recette avec 2 ingredients (salade et jambon)
        Assert.assertNull(recipe.getId());
        Ingredient salade = new Ingredient();
        salade.setLabel("salade");
        RecipeIngredient _1feuilleSalade = new RecipeIngredient();
        _1feuilleSalade.setIngredient(salade);
        _1feuilleSalade.setAmount("1 feuille");
        recipe.addIngredient(_1feuilleSalade);
        recipeDAO.save(recipe);
        Assert.assertTrue(recipe.getId() > 0);
        flushSession();
        entityManager.clear();

        // detache la recette a modifier
        Recipe searchedRecipe = detachRecipe(recipe);
        assertEquals(2, searchedRecipe.getRecipeIngredients().size());
        // supprime un ingredient
        Iterator<RecipeIngredient> it = searchedRecipe.getRecipeIngredients()
                .iterator();
        it.next();
        it.remove();
        assertEquals(1, searchedRecipe.getRecipeIngredients().size());
        recipeDAO.merge(searchedRecipe);
        flushSession();
        entityManager.clear();

        recipe = recipeDAO.getEntity(Recipe.class, searchedRecipe.getId());
        Assert.assertEquals(1, recipe.getRecipeIngredients().size());
    }

    @Test
    public void testUpdateRecipeWithNewFirstStep() throws Exception {
        // enregistre la recette
        Assert.assertNull(recipe.getId());
        recipeDAO.save(recipe);
        Assert.assertTrue(recipe.getId() > 0);

        entityManager.clear();
        // load la recette a modifier
        Recipe searchedRecipe = detachRecipe(recipe);

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
        entityManager.clear();

        // recette2
        Recipe recipe2 = new Recipe();
        recipe2.setTitle("title2");
        recipe2.addIngredient(_400gJambon);
        recipe2.addRecipeStep(RecipeStep.builder().withLabel("step")
                .withOrder(1).build());

        recipeDAO.save(recipe2);
        flushSession();
        entityManager.clear();

        // asserts
        assertEquals(recipe2.getId(), recipeDAO.getLastRecipe().getId());
    }

    private Recipe detachRecipe(Recipe r) {

        Recipe detachedRecipe = new Recipe();
        detachedRecipe.setId(r.getId());
        detachedRecipe.setTitle(r.getTitle());

        Iterator<RecipeIngredient> it = r.getRecipeIngredients().iterator();
        while (it.hasNext()) {
            RecipeIngredient ri = it.next();
            Ingredient i = new Ingredient();
            i.setId(ri.getIngredient().getId());
            RecipeIngredient detachedRi = new RecipeIngredient(i,
                    ri.getAmount());
            detachedRecipe.addIngredient(detachedRi);
        }

        SortedSet<RecipeStep> recipeSteps = new TreeSet<RecipeStep>();
        recipeSteps.addAll(r.getRecipeSteps());
        detachedRecipe.setRecipeSteps(recipeSteps);

        return detachedRecipe;

    }

    @Test
    public void testGetRecipeById() throws Exception {
        // recette1
        recipeDAO.save(recipe);
        flushSession();
        entityManager.clear();

        Recipe fullRecipe = recipeDAO.getRecipeById(recipe.getId());
        entityManager.clear();

        assertEquals(recipe.getId(), fullRecipe.getId());
        for (RecipeIngredient ri : fullRecipe.getRecipeIngredients()) {
            assertFalse(ri.getPk().getIngredient() instanceof HibernateProxy);
        }
    }

    @Test
    public void testGetRecipeByUnknownId() throws Exception {

        Integer unknownId = new Integer(12345);
        Recipe notFoundRecipe = recipeDAO.getRecipeById(unknownId);
        entityManager.clear();

        assertNull(notFoundRecipe);

    }

}
