package fr.petitsplats.dao;

import fr.petitsplats.domain.Recipe;

public interface RecipeDAO extends DataAccessObject {

    public Recipe merge(Recipe recipe);

    public Recipe save(Recipe recipe);
}
