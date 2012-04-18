package fr.petitsplats.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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

    public RecipeStep(String label) {
        this.label = label;
    }

    public RecipeStep() {
        // emtpy
    }

    @Override
    public int compareTo(RecipeStep o) {
        if (id != null) {
            return id.compareTo(o.getId());
        }
        return 1;
    }
}
