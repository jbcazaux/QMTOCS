package fr.petitsplats.dao;

import java.util.Iterator;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import fr.petitsplats.domain.Ingredient;
import fr.petitsplats.domain.Recipe;
import fr.petitsplats.domain.RecipeIngredient;

@Repository
public class RecipeDAOImpl extends AbstractDAO implements RecipeDAO {
    // private Logger logger = LoggerFactory.getLogger(this.getClass());

    public Recipe merge(Recipe recipe) {

        reatachIngredients(recipe);
        getEntityManager().merge(recipe);
        return recipe;
    }

    public Recipe save(Recipe recipe) {

        reatachIngredients(recipe);
        getEntityManager().persist(recipe);
        return recipe;
    }

    /**
     * @param recipe
     */
    private void reatachIngredients(Recipe recipe) {

        for (Iterator<RecipeIngredient> it = recipe.getRecipeIngredients()
                .iterator(); it.hasNext();) {
            RecipeIngredient ri = it.next();
            Ingredient ingredient = ri.getIngredient();
            if (ingredient.getId() != null && ingredient.getId() != 0) {
                Ingredient reference = getEntityManager().getReference(
                        Ingredient.class, ingredient.getId());
                ri.setIngredient(reference);
            } else {
                getEntityManager().persist(ingredient);
            }
        }
    }

    @Override
    public Recipe getLastRecipe() {
        Query q = getEntityManager().createQuery("select max (id) from Recipe");

        Recipe recipe = null;
        try {
            Integer id = (Integer) q.getSingleResult();
            recipe = this.getRecipeById(id);
        } catch (NoResultException nre) {
            getLogger().info("no result found");
        }
        return recipe;
    }

    public Recipe getRecipeById(Integer recipeId) {
        Query q = getEntityManager().createQuery(
                "select r from Recipe as r "
                        + "left join fetch r.recipeIngredients as ri "
                        + "left join fetch ri.pk as pk "
                        + "left join fetch pk.ingredient "
                        + "left join fetch r.recipeSteps as rs "
                        + "where r.id = :recipeId");

        q.setParameter("recipeId", recipeId);

        Recipe recipe = null;
        try {
            recipe = (Recipe) q.getSingleResult();
        } catch (NoResultException nre) {
            getLogger().info("no result found");
            return null;
        }
        return initializeAndUnproxyRecipe(recipe);
    }

    private Recipe initializeAndUnproxyRecipe(Recipe recipe) {

        recipe = initializeAndUnproxy(recipe);
        for (RecipeIngredient ri : recipe.getRecipeIngredients()) {
            ri.setIngredient(initializeAndUnproxy(ri.getPk().getIngredient()));
        }

        return recipe;
    }

}
