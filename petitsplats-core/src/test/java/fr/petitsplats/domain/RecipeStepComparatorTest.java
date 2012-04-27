package fr.petitsplats.domain;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class RecipeStepComparatorTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testCompareTo() throws Exception {

        RecipeStep r1 = new RecipeStep.Builder().withOrder(1).build();
        RecipeStep r2 = new RecipeStep.Builder().withOrder(2).build();
        RecipeStep r3 = new RecipeStep.Builder().build();

        assertTrue(r1.compareTo(r2) < 0);
        assertTrue(r1.compareTo(r3) > 0);
        assertTrue(r2.compareTo(r3) > 0);
        assertTrue(r3.compareTo(r1) < 0);
        assertTrue(r1.compareTo(r1) == 0);
        assertTrue(r3.compareTo(r3) == 0);
    }

}
