package fr.petitsplats.web.controllers;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.petitsplats.domain.Recipe;
import fr.petitsplats.exception.ViolationException;
import fr.petitsplats.service.RecipeService;

@Controller
@RequestMapping(value = "/recipe")
public class RecipeController extends AbstractController {

    @Autowired
    private RecipeService recipeService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Recipe getRecipe(@PathVariable int id) {

        // Recipe recipe = recipeDAO.getEntity(Recipe.class, id);
        Recipe recipe = new Recipe();
        return recipe;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public int createRecipe(@RequestBody Recipe recipe,
            HttpServletResponse response) throws ViolationException {
        int id = recipeService.createRecipe(recipe);

        response.setStatus(HttpServletResponse.SC_OK);
        return id;
    }

}
