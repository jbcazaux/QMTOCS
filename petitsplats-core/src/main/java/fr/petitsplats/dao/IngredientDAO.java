package fr.petitsplats.dao;

import java.util.List;

import fr.petitsplats.domain.Ingredient;

public interface IngredientDAO extends DataAccessObject {

    public List<Ingredient> findAll();

}
