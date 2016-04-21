package cfa.vo.vodml.io;

import org.junit.Test;
import org.xml.sax.SAXParseException;

import java.net.URL;

import static org.junit.Assert.*;

public class ValidatorTest {

    @Test
    public void testValidator() throws Exception {
        URL document = getClass().getResource("/source.vodml.xml");
        Validator v = new Validator();
        assertTrue(v.validate(document));
    }

    @Test
    public void testInvalid() throws Exception {
        URL document = getClass().getResource("/source-invalid.vodml.xml");
        Validator v = new Validator();
        try {
            v.validate(document);
        } catch (SAXParseException ex) {
            return; // should fail
        }
        fail("Should have thrown exception");
    }

}
