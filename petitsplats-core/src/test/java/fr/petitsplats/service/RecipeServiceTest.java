package fr.petitsplats.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
}
