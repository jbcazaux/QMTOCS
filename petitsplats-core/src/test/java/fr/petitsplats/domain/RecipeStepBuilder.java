package fr.petitsplats.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RecipeStepBuilder {

    @Test
    public void testBuilder() {

        Integer id = 12;
        String label = "label13";
        Integer order = 14;
        RecipeStep rs = RecipeStep.builder().withId(id).withLabel(label)
                .withOrder(order).build();

        assertEquals(id, rs.getId());
        assertEquals(label, rs.getLabel());
        assertEquals(order, rs.getOrder());

    }
}
