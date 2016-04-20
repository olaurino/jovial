package cfa.vo.vodml.io;

import cfa.vo.vodml.Model;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.joda.time.DateTime;
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
        model = new Model();
        model.setName("something");
        model.setTitle("Some Title");
        model.setDescription("Some Description");
        model.setVersion("1.0-SNAPSHOT");
        model.setLastModified(new DateTime("2016-04-16T10:16:50.000-04:00"));
        model.getAuthors().add("John Doe");

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
                "  <vo-dml:model xmlns:vo-dml=\"http://www.ivoa.net/xml/VODML/v1.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.ivoa.net/xml/VODML/v1.0 http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/xsd/vo-dml-v1.0.xsd\">\n" +
                "    <name>something</name>\n" +
                "    <title>Some Title</title>\n" +
                "    <version>1.0-SNAPSHOT</version>\n" +
                "    <lastModified>2016-04-16T10:16:50.000-04:00</lastModified>\n" +
                "    <author>John Doe</author>\n" +
                "    <description>Some Description</description>\n" +
                "  </vo-dml:model>";
    }
}
