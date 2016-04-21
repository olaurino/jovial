//package cfa.vo.vodml.instance;
//
//import cfa.vo.vodml.ModelSpec;
//import cfa.vo.vodml.io.XmlReader;
//import cfa.vo.vodml.io.XmlWriter;
//import org.custommonkey.xmlunit.XMLAssert;
//import org.custommonkey.xmlunit.XMLUnit;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.io.ByteArrayOutputStream;
//import java.io.OutputStream;
//import java.net.URL;
//
//public class InstanceModelJavaTest {
//    private XmlWriter writer;
//    private OutputStream os;
//    private ModelSpec modelSpec;
//
//    @Before
//    public void setUp() throws Exception {
//        XMLUnit.setIgnoreWhitespace(true);
//        writer = new XmlWriter();
//        os = new ByteArrayOutputStream();
//        modelSpec = new XmlReader().read(getClass().getResource("/DatasetMetadata-1.0.vo-dml.xml").openStream());
//    }
//
//    @Test
//    public void testPreamble() throws Exception {
//        Instance in = new Instance();
//        Model model = new Model(modelSpec, new URL("http://some/where/dataset.vo-dml.xml"), null);
//        in.getModels().add(model);
//        writer.write(in, os);
//        String expected = InstanceModelTest.preamble();
//        String actual = os.toString();
//        XMLAssert.assertXMLEqual(actual, expected);
//    }
//}