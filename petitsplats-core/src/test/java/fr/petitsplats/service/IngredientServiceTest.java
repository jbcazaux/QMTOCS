package fr.petitsplats.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fr.petitsplats.dao.IngredientDAO;

@RunWith(MockitoJUnitRunner.class)
public class IngredientServiceTest {

    @Mock
    private IngredientDAO ingredientDAO;

    private IngredientService ingredientService;

    @Before
    public void setUp() throws Exception {
        ingredientService = new IngredientService();
        ingredientService.setIngredientDAO(ingredientDAO);
    }

    @Test
    public void testFindAll() throws Exception {
        ingredientService.findAll();

        verify(ingredientDAO, times(1)).findAll();
    }

    @Test
    public void testFindByLabel() throws Exception {
        ingredientService.findByLabel("label");
        verify(ingredientDAO, times(1)).findByLabel("label");
    }
}
