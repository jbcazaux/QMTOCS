package fr.petitsplats.domain;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.BeforeClass;
import org.junit.Test;

public class RecipeValidationTest {

    private static Validator validator;

    @BeforeClass
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidation() {
        Recipe r = new Recipe();
        Set<ConstraintViolation<Recipe>> constraintsViolations = validator
                .validate(r);
        assertEquals(3, constraintsViolations.size());
    }

}
