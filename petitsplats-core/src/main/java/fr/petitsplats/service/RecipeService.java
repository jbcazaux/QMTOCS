package fr.petitsplats.service;

import lombok.Setter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.petitsplats.dao.RecipeDAO;
import fr.petitsplats.domain.Recipe;

@Transactional
@Service
public class RecipeService {

    @Setter
    @Autowired
    private RecipeDAO recipeDAO;

    public Integer createRecipe(Recipe recipe) {
        recipeDAO.save(recipe);
        return recipe.getId();
    }

}
