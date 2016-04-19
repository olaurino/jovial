package cfa.vo.vodml.io;

import org.junit.Test;
import java.net.URL;

import static org.junit.Assert.*;

public class ValidatorTest {

    @Test
    public void testValidator() throws Exception {
        URL document = getClass().getResource("/source.vodml.xml");
        Validator v = new Validator();
        assertTrue(v.validate(document));
    }

}
