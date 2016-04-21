package cfa.vo.vodml.io

import cfa.vo.vodml.Model
import cfa.vo.vodml.instance.ObjectInstance
import cfa.vo.vodml.instance.ValueInstance
import cfa.vo.vodml.instance.Votable
import org.custommonkey.xmlunit.XMLUnit
import org.junit.Before
import org.junit.Test


class VoTableBuilderTest {
    def writer = new VodmlWriter()
    ByteArrayOutputStream os = new ByteArrayOutputStream()
    Model modelSpec;
    String org = "ds:party.Organization"

    @Before
    void setUp() {
        XMLUnit.setIgnoreWhitespace(true);
        def reader = new VodmlReader()
        modelSpec = reader.read(getClass().getResource("/DatasetMetadata-1.0.vo-dml.xml").openStream())
//        model = new Model(modelSpec, new URL("http://some/where/dataset.vo-dml.xml"), null)
    }

    @Test
    void testPreamble() {
        def builder = new VoTableBuilder()

        def instance = builder.votable {
            model(modelSpec)
            objectInstance("$org") {
                valueInstance(role: "name", value:"OrgName")
                valueInstance(role: "address", value:"An Address")
            }
        }

        def expected = new Votable()
        expected.model = modelSpec
        def obj = new ObjectInstance("$org")
        obj.attributes << new ValueInstance(role: "ds:party.Party.name", value:"OrgName")
        obj.attributes << new ValueInstance(role: "${org}.address", value:"An Address")
        expected.objectTypes << obj

        assert instance == expected
    }

    public static String preamble() {
        """<?xml version="1.0" encoding="UTF-8"?>
           <VOTABLE xmlns="http://www.ivoa.net/xml/VOTable/v1.2"
                      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                      xsi:schemaLocation="http://www.ivoa.net/xml/VOTable/v1.2 http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/xsd/vo-dml-v1.0.xsd">
              <RESOURCE>
                <GROUP>
                  <VODML>
                    <TYPE>vo-dml:Model</TYPE>
                  </VODML>
                  <PARAM value="ds"/>
                  <VODML>
                    <ROLE>vo-dml:Model.name</ROLE>
                  </VODML>
                  <PARAM value="0.x"/>
                  <VODML>
                    <ROLE>vo-dml:Model.version</ROLE>
                  </VODML>
                  <PARAM value="http://some/where/dataset.vo-dml.xml"/>
                  <VODML>
                    <ROLE>vo-dml:Model.url</ROLE>
                  </VODML>
                </GROUP>
              </RESOURCE>
           </VOTABLE>"""
    }

    public static String datatype() {
        '''<?xml version="1.0" encoding="UTF-8"?>
           <VOTABLE xmlns="http://www.ivoa.net/xml/VOTable/v1.2"
                      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                      xsi:schemaLocation="http://www.ivoa.net/xml/VOTable/v1.2 http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/xsd/vo-dml-v1.0.xsd">
              <RESOURCE>
                <GROUP>
                  <VODML>
                    <TYPE>vo-dml:Model</TYPE>
                  </VODML>
                  <PARAM value="ds"/>
                  <VODML>
                    <ROLE>vo-dml:Model.name</ROLE>
                  </VODML>
                  <PARAM value="0.x"/>
                  <VODML>
                    <ROLE>vo-dml:Model.version</ROLE>
                  </VODML>
                  <PARAM value="http://some/where/dataset.vo-dml.xml"/>
                  <VODML>
                    <ROLE>vo-dml:Model.url</ROLE>
                  </VODML>
                  <GROUP>
                    <VODML><TYPE>ds:dataset.Dataset</TYPE></VODML>
                    <PARAM name="dataProductType" value="ds:dataset.DataProductType.CUBE"/>
                      <VODML>
                        <ROLE>ds:dataset.Dataset.dataProductType</ROLE>
                        <TYPE>ds:dataset.DataProductType</TYPE>
                      </VODML>
                  </GROUP>
                </GROUP>
              </RESOURCE>
           </VOTABLE>'''
    }
}
