package fr.petitsplats.dao;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import fr.petitsplats.domain.Recipe;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring-context.xml",
        "classpath:/datasource-test.xml" })
@Transactional
public class RecipeDAOTest {

    @Autowired
    private RecipeDAO recipeDAO;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testSave() {
        Recipe r = new Recipe();
        r.setTitle("title");
        r.setImageId(null);

        recipeDAO.toto();
        recipeDAO.save(r);
        recipeDAO.toto();

        r = recipeDAO.getEntity(r.getClass(), 1);
        Assert.assertEquals(1, r.getId());
    }

}
