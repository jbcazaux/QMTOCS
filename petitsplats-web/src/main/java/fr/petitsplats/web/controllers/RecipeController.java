package fr.petitsplats.web.controllers;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.petitsplats.dao.RecipeDAO;
import fr.petitsplats.domain.Recipe;

@Controller
@RequestMapping(value = "/recipe")
public class RecipeController {

    @Autowired
    private RecipeDAO recipeDAO;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Recipe getRecipe(@PathVariable int id) {

        // Recipe recipe = recipeDAO.getEntity(Recipe.class, id);
        Recipe recipe = new Recipe();
        return recipe;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public int createRecipe(Recipe recipe, HttpServletResponse response) {
        recipeDAO.save(recipe);
        response.setStatus(HttpServletResponse.SC_OK);
        return recipe.getId();
    }

}
