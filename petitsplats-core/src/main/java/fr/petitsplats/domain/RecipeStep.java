package fr.petitsplats.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@EqualsAndHashCode(of = { "id", "label" }, callSuper = false)
public class RecipeStep extends AbstractEntity implements
        Comparable<RecipeStep> {

    @Id
    @Column(name = "recipestep_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    @NotEmpty
    private String label;

    @Getter
    @Setter
    @NotNull
    @Min(value = 1)
    @Column(name = "steporder")
    private Integer order;

    @Override
    public int compareTo(RecipeStep o) {
        if (order != null) {
            return order.compareTo(o.getOrder());
        }
        return 1;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private RecipeStep recipeStep;

        public Builder() {
            recipeStep = new RecipeStep();
        }

        public Builder withId(Integer id) {
            recipeStep.setId(id);
            return this;
        }

        public Builder withLabel(String label) {
            recipeStep.setLabel(label);
            return this;
        }

        public Builder withOrder(Integer order) {
            recipeStep.setOrder(order);
            return this;
        }

        public RecipeStep build() {
            return recipeStep;
        }
    }
}
