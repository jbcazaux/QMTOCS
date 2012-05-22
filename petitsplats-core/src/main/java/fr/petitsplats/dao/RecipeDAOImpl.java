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
     * Cette méthode ne peut être appelée que sur une recette détachée de la
     * session, sinon bug hibernate: impossible de supprimer l ingredient de la
     * liste.
     * 
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
            recipe = this.getEntity(Recipe.class, id);
        } catch (NoResultException nre) {
            getLogger().info("no result found");
        }
        return recipe;
    }

}
