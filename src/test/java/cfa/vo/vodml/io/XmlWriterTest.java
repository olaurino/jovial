package cfa.vo.vodml.io;

import cfa.vo.vodml.Model;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class XmlWriterTest {
    private Model model;
    private XmlWriter writer;
    private String expected;

    @Before
    public void setUp() throws Exception {
        Calendar date = new GregorianCalendar(2016, Calendar.APRIL, 16, 10, 16, 50);
        model = new Model();
        model.setName("something");
        model.setTitle("Some Title");
        model.setDescription("Some Description");
        model.setVersion("1.0-SNAPSHOT");
        model.setLastModified(date.getTime());

        writer = new XmlWriter();
        expected = makeString();

        XMLUnit.setIgnoreWhitespace(true);
    }

    @Test
    public void testWrite() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        writer.write(model, os);
        String out = os.toString("UTF-8");
        XMLAssert.assertXMLEqual(expected, out);
    }

    private String makeString() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "  <model>\n" +
                "    <name>something</name>\n" +
                "    <title>Some Title</title>\n" +
                "    <version>1.0-SNAPSHOT</version>\n" +
                "    <lastModified>2016-04-16T10:16:50</lastModified>\n" +
                "    <description>Some Description</description>\n" +
                "  </model>";
    }
}
