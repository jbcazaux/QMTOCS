package fr.petitsplats.dao;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.springframework.stereotype.Repository;

import fr.petitsplats.domain.Ingredient;
import fr.petitsplats.domain.Recipe;

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

    private void reatachIngredients(Recipe recipe) {
        Set<Ingredient> proxyIngredients = new HashSet<Ingredient>();

        boolean atLeastOneProxy = false;
        for (Iterator<Ingredient> it = recipe.getIngredients().iterator(); it
                .hasNext();) {
            Ingredient ingredient = it.next();
            if (ingredient.getId() != null && ingredient.getId() != 0) {
                Ingredient reference = getEntityManager().getReference(
                        Ingredient.class, ingredient.getId());
                proxyIngredients.add(reference);
                atLeastOneProxy = true;
            } else {
                proxyIngredients.add(ingredient);
            }
        }

        if (!proxyIngredients.isEmpty() && atLeastOneProxy) {
            recipe.getIngredients().clear();
            recipe.getIngredients().addAll(proxyIngredients);
        }
    }
}
