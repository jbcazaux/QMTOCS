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
import fr.petitsplats.domain.RecipePicture;
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

        validate(recipe);
        reattachIdLessIngredients(recipe);
        recipeDAO.save(recipe);
        return recipe.getId();
    }

    public void updateRecipe(Recipe recipe) throws ViolationException {
        validate(recipe);
        reattachIdLessIngredients(recipe);
        recipeDAO.merge(recipe);
    }

    public Recipe findById(Integer id) {
        return recipeDAO.getEntity(Recipe.class, id);
    }

    public Integer createRecipePicture(RecipePicture recipePicture)
            throws ViolationException {

        validate(recipePicture);

        if (recipeDAO.getEntity(Recipe.class, recipePicture.getId()) == null) {
            throw new IllegalArgumentException("Cannot find recipe with id "
                    + recipePicture.getId());
        }

        recipeDAO.save(recipePicture);
        return recipePicture.getId();
    }

    public RecipePicture findPictureById(Integer id) {
        return recipeDAO.getEntity(RecipePicture.class, id);
    }

    private <T> void validate(T recipe) throws ViolationException {
        Set<ConstraintViolation<T>> violations = validator.validate(recipe);
        if (!CollectionUtils.isEmpty(violations)) {
            throw new ViolationException(violations);
        }
    }

    /**
     * Scanne les ingrédients d'une recette pour voir si les ingrédients
     * attachés qui n ont pas d'id n existent vraiment pas en base
     * 
     * @param recipe
     */
    private void reattachIdLessIngredients(Recipe recipe) {
        for (Ingredient ingredient : recipe.getIngredients()) {
            if (ingredient.getId() == null || ingredient.getId() == 0) {
                Ingredient i = ingredientService.findByLabel(ingredient
                        .getLabel());
                if (i != null) {
                    ingredient.setId(i.getId());
                }
            }
        }
    }
}
