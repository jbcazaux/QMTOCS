package fr.petitsplats.web.controllers;

import java.util.Collections;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.petitsplats.domain.Ingredient;
import fr.petitsplats.domain.Recipe;
import fr.petitsplats.domain.RecipeStep;

@Controller
@RequestMapping(value = "/recipe")
public class RecipeController {

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Recipe showIndex(@PathVariable int id) {

        // FIXME unmock !

        Ingredient jambon = new Ingredient();
        jambon.setName("jambon");

        RecipeStep cuisson = new RecipeStep("faire cuire les pates");
        RecipeStep pret = new RecipeStep("c'est pret");

        Recipe recipe = new Recipe();

        recipe = new Recipe();
        recipe.setTitle("jambon-pate");
        recipe.setImageId(12);
        recipe.setIngredients(Collections.singletonList(jambon));
        recipe.addRecipeStep(cuisson);
        recipe.addRecipeStep(pret);
        recipe.setId(id);

        return recipe;
    }
}
