package cfa.vo.vodml.instance

import cfa.vo.vodml.io.XmlWriter
import org.custommonkey.xmlunit.XMLAssert
import org.custommonkey.xmlunit.XMLUnit
import org.junit.Before
import org.junit.Test


class InstanceModelTest {
    def writer = new XmlWriter()
    def os = new ByteArrayOutputStream()

    @Before
    void setUp() {
        XMLUnit.setIgnoreWhitespace(true);
    }

    @Test
    void testPreamble() {
        Instance datasetInstance = new Instance(
                models: [
                        new ModelLocation(
                                vodmlUrl: new URL("http://some/where/dataset.vo-dml.xml"),
                                prefix: "ds",
                                version: "1.0"
                        )
                ]
        )
        writer.write(datasetInstance, os)

        def expected = preamble()
        def actual = os.toString("UTF-8")

        XMLAssert.assertXMLEqual(actual, expected)
    }

    public static String preamble() {
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><VOTABLE xmlns=\"http://www.ivoa.net/xml/VOTable/v1.2\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.ivoa.net/xml/VOTable/v1.2 http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/xsd/vo-dml-v1.0.xsd\">\n" +
                "  <RESOURCE>\n" +
                "    <GROUP>\n" +
                "      <VODML>\n" +
                "        <TYPE>vo-dml:Model</TYPE>\n" +
                "      </VODML>\n" +
                "      <PARAM value=\"ds\"/>\n" +
                "      <VODML>\n" +
                "        <ROLE>vo-dml:Model.name</ROLE>\n" +
                "      </VODML>\n" +
                "      <PARAM value=\"1.0\"/>\n" +
                "      <VODML>\n" +
                "        <ROLE>vo-dml:Model.version</ROLE>\n" +
                "      </VODML>\n" +
                "      <PARAM value=\"http://some/where/dataset.vo-dml.xml\"/>\n" +
                "      <VODML>\n" +
                "        <ROLE>vo-dml:Model.url</ROLE>\n" +
                "      </VODML>\n" +
                "    </GROUP>\n" +
                "  </RESOURCE>\n" +
                "</VOTABLE>".stripIndent()
    }
}
