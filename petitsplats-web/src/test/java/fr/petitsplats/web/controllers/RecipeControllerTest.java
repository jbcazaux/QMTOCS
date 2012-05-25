package fr.petitsplats.web.controllers;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import fr.petitsplats.MethodNotAllowedException;
import fr.petitsplats.domain.Recipe;
import fr.petitsplats.exception.ViolationException;
import fr.petitsplats.service.RecipeService;

@RunWith(MockitoJUnitRunner.class)
public class RecipeControllerTest {

    @Autowired
    private RecipeController recipeController;

    @Mock
    HttpServletResponse response;
    @Mock
    RecipeService recipeService;

    @Before
    public void setUp() throws Exception {
        recipeController = new RecipeController();
        recipeController.setRecipeService(recipeService);
    }

    @Test
    public void testCreateInsteadOfUpdate() {
        Recipe r = new Recipe();
        r.setId(12);
        try {
            recipeController.createRecipe(r, response);
            fail("exception expected");
        } catch (MethodNotAllowedException e) {
            // ok
        } catch (Exception e) {
            fail("exception not expected");
        }
    }

    @Test
    public void testCreateWithId0() throws ViolationException,
            MethodNotAllowedException {
        Recipe r = new Recipe();
        r.setId(new Integer(0));
        recipeController.createRecipe(r, response);
        verify(recipeService, times(1)).createRecipe(r);
    }

    @Test
    public void testCreateWithIdNull() throws ViolationException,
            MethodNotAllowedException {
        Recipe r = new Recipe();
        r.setId(null);
        recipeController.createRecipe(r, response);
        verify(recipeService, times(1)).createRecipe(r);
    }

    @Test
    public void testGetRecipe() throws Exception {
        Recipe r = new Recipe();
        r.setId(12);
        when(recipeService.findById(r.getId())).thenReturn(r);
        recipeController.getRecipe(r.getId(), response);
        verify(recipeService, times(1)).findById(r.getId());
    }

    @Test
    public void testGetUnknownRecipe() throws Exception {
        Recipe r = new Recipe();
        r.setId(12);
        when(recipeService.findById(r.getId())).thenReturn(null);
        recipeController.getRecipe(r.getId(), response);
        verify(recipeService, times(1)).findById(r.getId());
    }

    @Test
    public void testUpdateInsteadOfCreate() {
        Recipe r = new Recipe();
        r.setId(0);
        try {
            recipeController.updateRecipe(r, 0, response);
            fail("exception expected");
        } catch (MethodNotAllowedException e) {
            // ok
        } catch (Exception e) {
            fail("exception not expected");
        }
    }

    @Test
    public void testUpdateWithBadId() {
        Recipe r = new Recipe();
        r.setId(12);
        try {
            recipeController.updateRecipe(r, r.getId() + 1, response);
            fail("exception expected");
        } catch (MethodNotAllowedException e) {
            // ok
        } catch (Exception e) {
            fail("exception not expected");
        }
    }

    @Test
    public void testUpdateRecipe() throws ViolationException,
            MethodNotAllowedException {
        Recipe r = new Recipe();
        r.setId(12);
        r.setTitle("title");
        recipeController.updateRecipe(r, r.getId(), response);
        verify(recipeService).updateRecipe(r);
    }
}
