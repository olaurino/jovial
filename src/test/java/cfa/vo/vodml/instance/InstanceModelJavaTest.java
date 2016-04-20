package cfa.vo.vodml.instance;

import cfa.vo.vodml.io.XmlWriter;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URL;

import static org.junit.Assert.*;

public class InstanceModelJavaTest {
    private XmlWriter writer = new XmlWriter();
    private OutputStream os = new ByteArrayOutputStream();

    @Before
    public void setUp() throws Exception {
        XMLUnit.setIgnoreWhitespace(true);
    }

    @Test
    public void testPreamble() throws Exception {
        Instance in = new Instance();
        ModelLocation model = new ModelLocation("ds", "1.0", new URL("http://some/where/dataset.vo-dml.xml"), null);
        in.getModels().add(model);
        writer.write(in, os);
        String expected = InstanceModelTest.preamble();
        String actual = os.toString();
        XMLAssert.assertXMLEqual(actual, expected);
    }
}