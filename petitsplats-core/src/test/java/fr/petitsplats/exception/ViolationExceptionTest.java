package fr.petitsplats.exception;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Path;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ViolationExceptionTest {

    @Mock
    private ConstraintViolation<String> cs;

    @Mock
    private Path path;

    @Before
    public void setUp() throws Exception {
        when(path.toString()).thenReturn("property");
        when(cs.getPropertyPath()).thenReturn(path);
        when(cs.getMessage()).thenReturn("message d erreur");
    }

    @Test
    public void testViolationException() throws Exception {

        Set<ConstraintViolation<String>> set = Collections.singleton(cs);
        ViolationException ex = new ViolationException(set);

        Set<String[]> violations = ex.getViolations();
        assertEquals(path.toString(), violations.iterator().next()[0]);
        assertEquals(cs.getMessage(), violations.iterator().next()[1]);
        assertEquals(1, violations.size());

    }

}
