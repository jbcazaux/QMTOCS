package fr.petitsplats.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.petitsplats.domain.Recipe;

@Controller
@RequestMapping(value = "/recipe")
public class RecipeController {

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Recipe showIndex(@PathVariable int id) {

        Recipe recipe = new Recipe();
        recipe.setId(id);
        recipe.setTitle("recipe " + id);

        return recipe;
    }
}
