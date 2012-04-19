package fr.petitsplats.domain;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@EqualsAndHashCode(of = { "id" }, callSuper = false)
public class RecipePicture extends AbstractEntity {

    @Id
    @Column(name = "recipe_id")
    @NotNull
    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    @NotEmpty
    @Basic(fetch = FetchType.LAZY)
    @Lob
    @Column(length = 1048576)
    private byte[] image;

}
