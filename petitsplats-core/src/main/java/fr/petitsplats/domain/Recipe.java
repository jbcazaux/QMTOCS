package fr.petitsplats.domain;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "recipe")
@EqualsAndHashCode(of = { "id", "title" }, callSuper = false)
public class Recipe extends AbstractEntity {

    @Id
    @Column(name = "recipe_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    @NotEmpty
    private String title;

    @Getter
    @Setter
    @NotEmpty
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(name = "recipe_ingredients", joinColumns = { @JoinColumn(name = "recipe_id", referencedColumnName = "recipe_id") }, inverseJoinColumns = { @JoinColumn(name = "ingredient_id", referencedColumnName = "ingredient_id") })
    private Set<Ingredient> ingredients = new HashSet<Ingredient>();

    public void addIngredient(Ingredient i) {
        ingredients.add(i);
    }

    @Getter
    @Setter
    @NotEmpty
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinTable(name = "recipe_recipestep", joinColumns = { @JoinColumn(name = "recipe_id", referencedColumnName = "recipe_id") }, inverseJoinColumns = { @JoinColumn(name = "recipestep_id", referencedColumnName = "recipestep_id") })
    @Sort(type = SortType.NATURAL)
    private SortedSet<RecipeStep> recipeSteps = new TreeSet<RecipeStep>();

    public void addRecipeStep(RecipeStep rs) {
        recipeSteps.add(rs);
    }

}
