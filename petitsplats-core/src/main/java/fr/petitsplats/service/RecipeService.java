package fr.petitsplats.service;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import lombok.Setter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import fr.petitsplats.dao.RecipeDAO;
import fr.petitsplats.domain.Ingredient;
import fr.petitsplats.domain.Recipe;
import fr.petitsplats.exception.FunctionnalException;
import fr.petitsplats.exception.ViolationException;

@Transactional(rollbackFor = FunctionnalException.class)
@Service
public class RecipeService {

    @Setter
    @Autowired
    private Validator validator;

    @Setter
    @Autowired
    private RecipeDAO recipeDAO;

    @Setter
    @Autowired
    private IngredientService ingredientService;

    public Integer createRecipe(Recipe recipe) throws ViolationException {

        validateRecipe(recipe);

        for (Ingredient ingredient : recipe.getIngredients()) {
            if (ingredient.getId() == null || ingredient.getId() == 0) {
                Ingredient i = ingredientService.findByLabel(ingredient
                        .getLabel());
                if (i != null) {
                    ingredient.setId(i.getId());
                }
            }
        }
        recipeDAO.save(recipe);
        return recipe.getId();
    }

    private void validateRecipe(Recipe recipe) throws ViolationException {
        Set<ConstraintViolation<Recipe>> violations = validator
                .validate(recipe);
        if (!CollectionUtils.isEmpty(violations)) {
            throw new ViolationException(violations);
        }

    }
}
