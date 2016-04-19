package cfa.vo.vodml.io

import org.junit.Before
import org.junit.Test
import org.uispec4j.xml.XmlAssert


class XmlReaderTest {
    private input
    private output

    @Before
    void setUp() {
        def resource = getClass().getResource("/source.vodml.xml")
        def model = new XmlReader().read(resource.openStream())
        input = resource.openStream().text
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        new XmlWriter().write(model, os)
        output = os.toString("UTF-8")
    }

    @Test
    void testRoundtrip() {
        XmlAssert.assertEquals(input, output)
    }
}
