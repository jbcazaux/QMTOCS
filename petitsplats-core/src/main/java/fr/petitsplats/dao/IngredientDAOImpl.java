package fr.petitsplats.dao;

import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import fr.petitsplats.domain.Ingredient;

@Repository
public class IngredientDAOImpl extends AbstractDAO implements IngredientDAO {

    public List<Ingredient> findAll() {

        TypedQuery<Ingredient> allIngredients = getEntityManager().createQuery(
                "from Ingredient", Ingredient.class);
        return allIngredients.getResultList();
    }

}
