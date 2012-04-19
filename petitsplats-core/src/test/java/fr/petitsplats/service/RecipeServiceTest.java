package fr.petitsplats.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validator;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import fr.petitsplats.dao.RecipeDAO;
import fr.petitsplats.domain.Ingredient;
import fr.petitsplats.domain.Recipe;
import fr.petitsplats.domain.RecipePicture;
import fr.petitsplats.exception.ViolationException;

@RunWith(MockitoJUnitRunner.class)
public class RecipeServiceTest {

    @Mock
    private RecipeDAO recipeDAO;

    @Mock
    private IngredientService ingredientService;

    @Mock
    Validator validator;

    private RecipeService recipeService;
    private RecipePicture recipePicture;

    @Before
    public void setUp() throws Exception {

        recipeService = new RecipeService();
        recipeService.setRecipeDAO(recipeDAO);
        recipeService.setIngredientService(ingredientService);
        recipeService.setValidator(validator);

        recipePicture = new RecipePicture();
        recipePicture.setId(12);
        recipePicture.setImage(IOUtils.toByteArray(getClass()
                .getResourceAsStream("/google.png")));

    }

    @Test
    public void testCreateRecipeCallsDAO() throws ViolationException {

        Recipe r = new Recipe();
        recipeService.createRecipe(r);

        verify(recipeDAO, times(1)).save(r);
    }

    @Test
    public void testCreateRecipeWithValidationError() {
        ConstraintViolation<Recipe> cv = mock(ConstraintViolation.class);
        Path p = mock(Path.class);
        Recipe r = new Recipe();

        when(validator.validate(r)).thenReturn(Collections.singleton(cv));
        when(p.toString()).thenReturn("");
        when(cv.getPropertyPath()).thenReturn(p);
        when(cv.getMessage()).thenReturn("");

        try {
            recipeService.createRecipe(r);
            fail("exception expected");
        } catch (ViolationException e) {
            // ok
        }

        verify(recipeDAO, times(0)).save(r);
    }

    @Test
    public void testCreateRecipeWithExistingIngredientIdLess()
            throws ViolationException {

        Ingredient i = new Ingredient();
        i.setId(null);
        i.setLabel("ingredient1");
        Ingredient ii = new Ingredient();
        ii.setId(12);
        ii.setLabel("ingredient1");

        Recipe r = new Recipe();
        r.addIngredient(i);

        when(ingredientService.findByLabel(i.getLabel())).thenReturn(ii);

        recipeService.createRecipe(r);

        assertEquals(ii.getId(), r.getIngredients().get(0).getId());
        verify(recipeDAO, times(1)).save(r);
        verify(ingredientService, times(1)).findByLabel(i.getLabel());
    }

    @Test
    public void testCreateRecipeWithExistingIngredient()
            throws ViolationException {

        Ingredient i = new Ingredient();
        i.setId(12);
        i.setLabel("ingredient1");

        Recipe r = new Recipe();
        r.addIngredient(i);

        recipeService.createRecipe(r);

        assertEquals(i.getId(), r.getIngredients().get(0).getId());
        verify(recipeDAO, times(1)).save(r);
        verify(ingredientService, times(0)).findByLabel(Mockito.anyString());
    }

    @Test
    public void testFindById() throws Exception {
        Recipe r = new Recipe();
        r.setId(12);

        when(recipeDAO.getEntity(Recipe.class, r.getId())).thenReturn(r);

        recipeService.findById(r.getId());
        verify(recipeDAO, times(1)).getEntity(Recipe.class, r.getId());
    }

    @Test
    public void testFindByIdWithUnknownId() throws Exception {
        int id = 14;
        when(recipeDAO.getEntity(Recipe.class, id)).thenReturn(null);
        recipeService.findById(id);
        verify(recipeDAO, times(1)).getEntity(Recipe.class, id);
    }

    @Test
    public void testFindPictureById() throws Exception {
        RecipePicture rp = new RecipePicture();
        rp.setId(12);

        when(recipeDAO.getEntity(RecipePicture.class, rp.getId())).thenReturn(
                rp);

        recipeService.findPictureById(rp.getId());
        verify(recipeDAO, times(1)).getEntity(RecipePicture.class, rp.getId());
    }

    @Test
    public void testFindPictureByIdWithUnknownId() throws Exception {
        int id = 14;
        when(recipeDAO.getEntity(RecipePicture.class, id)).thenReturn(null);
        recipeService.findPictureById(id);
        verify(recipeDAO, times(1)).getEntity(RecipePicture.class, id);
    }

    @Test
    public void testCreateRecipePictureCallsDAO() throws ViolationException {
        when(recipeDAO.getEntity(Recipe.class, recipePicture.getId()))
                .thenReturn(new Recipe());
        recipeService.createRecipePicture(recipePicture);
        verify(recipeDAO, times(1)).save(recipePicture);
    }

    @Test
    public void testCreateRecipeValidation() throws ViolationException {
        when(recipeDAO.getEntity(Recipe.class, recipePicture.getId()))
                .thenReturn(new Recipe());
        recipeService.createRecipePicture(recipePicture);
        verify(recipeDAO, times(1)).save(recipePicture);
    }

    @Test
    public void testCreateRecipePictureWithValidationError() {
        ConstraintViolation<RecipePicture> cv = mock(ConstraintViolation.class);
        Path p = mock(Path.class);

        when(validator.validate(recipePicture)).thenReturn(
                Collections.singleton(cv));
        when(p.toString()).thenReturn("");
        when(cv.getPropertyPath()).thenReturn(p);
        when(cv.getMessage()).thenReturn("");

        try {
            recipeService.createRecipePicture(recipePicture);
            fail("exception expected");
        } catch (ViolationException e) {
            // ok
        }

        verify(recipeDAO, times(0)).save(recipePicture);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateRecipePictureWithNonExistingRecipeId()
            throws ViolationException {
        when(recipeDAO.getEntity(Recipe.class, recipePicture.getId()))
                .thenReturn(null);
        recipeService.createRecipePicture(recipePicture);
    }
}
