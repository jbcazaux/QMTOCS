package fr.petitsplats.web.controllers;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fr.petitsplats.service.IngredientService;

@RunWith(MockitoJUnitRunner.class)
public class IngredientControllerTest {

    @Mock
    private IngredientService ingredientService;

    private IngredientController ingredientController;

    @Before
    public void setUp() throws Exception {
        ingredientController = new IngredientController();
        ingredientController.setIngredientService(ingredientService);
    }

    @Test
    public void testFindAll() throws Exception {
        ingredientController.findAll();
        verify(ingredientService, times(1)).findAll();
    }

}
