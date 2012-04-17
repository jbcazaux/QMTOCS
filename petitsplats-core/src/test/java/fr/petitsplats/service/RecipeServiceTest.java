package fr.petitsplats.service;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fr.petitsplats.dao.RecipeDAO;
import fr.petitsplats.domain.Recipe;
import fr.petitsplats.exception.ViolationException;

@RunWith(MockitoJUnitRunner.class)
public class RecipeServiceTest {

    @Mock
    private RecipeDAO recipeDAO;

    @Mock
    Validator validator;

    private RecipeService recipeService;

    @Before
    public void setUp() throws Exception {

        recipeService = new RecipeService();
        recipeService.setRecipeDAO(recipeDAO);
        recipeService.setValidator(validator);

    }

    @Test
    public void testCreateRecipe() throws ViolationException {

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
}
