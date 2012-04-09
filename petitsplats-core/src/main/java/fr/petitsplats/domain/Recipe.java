package fr.petitsplats.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "recipe")
public class Recipe extends AbstractEntity {

    @Id
    @Column(name = "recipe_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private Integer imageId;

    @Getter
    @Setter
    @ManyToMany
    @JoinTable(name = "recipe_ingredients", joinColumns = { @JoinColumn(name = "recipe_id", referencedColumnName = "recipe_id") }, inverseJoinColumns = { @JoinColumn(name = "ingredient_id", referencedColumnName = "ingredient_id") })
    private List<Ingredient> ingredients;

    @Getter
    @Setter
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "recipe_recipestep", joinColumns = { @JoinColumn(name = "recipe_id", referencedColumnName = "recipe_id") }, inverseJoinColumns = { @JoinColumn(name = "recipestep_id", referencedColumnName = "recipestep_id") })
    private List<RecipeStep> recipeSteps = new ArrayList<RecipeStep>();

    public void addRecipeStep(RecipeStep rs) {
        recipeSteps.add(rs);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Recipe other = (Recipe) obj;
        if (id != other.id)
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        return true;
    }

}