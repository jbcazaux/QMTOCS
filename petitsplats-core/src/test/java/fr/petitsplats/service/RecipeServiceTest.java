package fr.petitsplats.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fr.petitsplats.dao.RecipeDAO;
import fr.petitsplats.domain.Recipe;

@RunWith(MockitoJUnitRunner.class)
public class RecipeServiceTest {

    @Mock
    private RecipeDAO recipeDAO;

    private RecipeService recipeService;

    @Before
    public void setUp() throws Exception {

        recipeService = new RecipeService();
        recipeService.setRecipeDAO(recipeDAO);
    }

    @Test
    public void testCreateRecipe() {

        Recipe r = new Recipe();
        recipeService.createRecipe(r);

        verify(recipeDAO, times(1)).save(r);
    }
}
