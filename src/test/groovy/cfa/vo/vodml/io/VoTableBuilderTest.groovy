package cfa.vo.vodml.io

import cfa.vo.vodml.metamodel.Model
import cfa.vo.vodml.instance.ModelInstance
import cfa.vo.vodml.instance.ObjectInstance
import cfa.vo.vodml.instance.ValueInstance
import cfa.vo.vodml.instance.VotableInstance
import org.custommonkey.xmlunit.XMLAssert
import org.custommonkey.xmlunit.XMLUnit
import org.junit.Before
import org.junit.Test


class VoTableBuilderTest {
    def writer = new VodmlWriter()
    ByteArrayOutputStream os = new ByteArrayOutputStream()
    Model dsSpec;
    Model stcSpec;
    Model charSpec;
    String org = "ds:party.Organization"

    @Before
    void setUp() {
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreComments(true);
        def reader = new VodmlReader()
        dsSpec = reader.read(getClass().getResource("/DatasetMetadata-1.0.vo-dml.xml").openStream())
        stcSpec = reader.read(new URL("http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/STC2/2016-02-19/VO-DML-STC2.vo-dml.xml").openStream())
    }

    @Test
    void testPreamble() {
        def builder = new VoTableBuilder()

        VotableInstance instance = builder.votable {
            model(spec: dsSpec, vodmlURL: "http://some/where/dataset.vo-dml.xml")
        }

        instance.toXml(os)
        String actual = os.toString("UTF-8")
        String expected = preamble("")
        XMLAssert.assertXMLEqual(expected, actual)
    }

    @Test
    void testDatasetInstance() {
        VotableInstance instance = new VoTableBuilder().votable {
            model(spec: dsSpec, vodmlURL: "http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/ds/DatasetMetadata-1.0.vo-dml.xml")
            model(spec: stcSpec, vodmlURL: "http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/STC2/2016-02-19/VO-DML-STC2.vo-dml.xml")
            object(type: "ds:experiment.Observation") {
                value(role: "observationID", value:"obsid.2015.73")
                collection(role: "target") {
                    object(type: "ds:experiment.AstroTarget") {
                        value(role: "name", value: "3C273")
                        value(role: "description", value: "A Quasar")
                        data(role: "position", type: "stc2:coords.Position") {
                            value(role: "value", value: [187.2792, 2.0525])
                        }
                        value(role: "objectClass", value: "BLAZAR")
                        value(role: "spectralClass", value: "Sy1")
                        value(role: "redshift", value: 0.158)
                        value(role: "varAmpl", value: Double.NaN)
                    }
                }
                collection(role: "obsConfig") {
                    object(type: "ds:experiment.ObsConfig") {
                        value(role: "bandpass", value: "optical")
                        value(role: "datasource", value: "survey")
                        collection(role: "instrument") {
                            object(type: "ds:experiment.Instrument") {
                                value(role: "name", value: "ACIS")
                            }
                        }
                        collection(role: "facility") {
                            object(type: "ds:experiment.Facility") {
                                reference(role: "party", value: "ACME")
                            }
                        }
                    }
                }
                collection(role: "proposal") {
                    object(type: "ds:experiment.Proposal") {
                        value(role: "identifier", value: "PROPOSAL/756/2014.06")
                    }
                }
                collection(role: "result") {
                    object(type: "ds:experiment.ObsDataset") {
                        value(role: "dataProductType", value: "ds:dataset.DataProductType.CUBE")
                        value(role: "dataProductSubtype", value: "MySubtype")
                        value(role: "calibLevel", value: 0)
                        collection(role: "characterisation") {
                            object(type: "ds:experiment.Characterisation")
                        }
                        collection(role: "derived") {
                            object(type: "ds:experiment.Derived") {
                                collection(role: "derivedElement") {
                                    object(type: "ds:experiment.DerivedScalar") {
                                        value(role: "name", value: "SNR")
                                        value(role: "value", value: 1.25)
                                    }
                                }
                            }
                        }
                        collection(role: "dataID") {
                            object(type: "ds:dataset.DataID") {
                                value(role: "title", value: "datasetTitle")
                                value(role: "datasetID", value: "ivo://some/uri")
                                value(role: "creatorDID", value: "me://some/other/uri")
                                value(role: "version", value: "DR3")
                                value(role: "date", value: "20160422T11:55:30")
                                value(role: "creationType", value: "ds:dataset.CreationType.ARCHIVAL")
                                collection(role: "collection") {
                                    object(type: "ds:dataset.Collection") {
                                        value(role: "name", value: "Data Release 3")
                                    }
                                }
                                collection(role: "contributor") {
                                    object(type: "ds:dataset.Contributor") {
                                        value(role: "acknowledgment", value: "Project Manager")
                                        reference(role: "party", value: "BILL")
                                    }
                                    object(type: "ds:dataset.Contributor") {
                                        value(role: "acknowledgment", value: "Bought the donuts!")
                                        reference(role: "party", value: "TOM")
                                    }
                                }
                                collection(role: "creator") {
                                    object(type: "ds:dataset.Creator") {
                                        reference(role: "party", value: "ACME")
                                    }
                                }
                            }
                        }
                        collection(role: "curation") {
                            object(type: "ds:dataset.Curation") {
                                value(role: "publisherDID", value: "me://some/other/uri")
                                value(role: "version", value: "DR3")
                                value(role: "releaseDate", value: "20160422T11:55:30")
                                value(role: "rights", value: "ds:dataset.RightsType.PUBLIC")
                                collection(role: "contact") {
                                    object(type: "ds:dataset.Contact") {
                                        reference(role: "party", value: "BILL")
                                    }
                                }
                                collection(role: "publisher") {
                                    object(type: "ds:dataset.Publisher") {
                                        value(role: "publisherID", value: "ivo://acme.org")
                                        reference(role: "party", value: "ACME")
                                    }
                                }
                                collection(role: "reference") {
                                    object(type: "ds:dataset.Publication") {
                                        value(role: "refCode", value: "ApJ12345")
                                    }
                                    object(type: "ds:dataset.Publication") {
                                        value(role: "refCode", value: "ApJ6789")
                                    }
                                }
                            }
                        }
                    }
                }
            }
            object(id: "ACME", type: "ds:party.Organization") {
                value(role: "name", value: "ACME edu")
                value(role: "address", value: "Colorado Blvd")
                value(role: "phone", value: "555-012-3456")
                value(role: "email", value: "helpdesk@acme.org")
                value(role: "logo", value: "http://acme.org/stunning.png")
            }
            object(id: "BILL", type: "ds:party.Individual") {
                value(role: "name", value: "William E. Coyote")
                value(role: "address", value: "Colorado Blvd")
                value(role: "phone", value: "555-654-3210")
                value(role: "email", value: "bill@acme.org")
            }
            object(id: "TOM", type: "ds:party.Individual") {
                value(role: "name", value: "Tom Ray")
                value(role: "address", value: "Colorado Blvd")
                value(role: "phone", value: "555-999-5555")
                value(role: "email", value: "bill@acme.org")
            }
            object(id: "COORDSYS", type: "stc2:coordsystem.AstroCoordSystem")
        }

        instance.toXml(System.out)
    }

    @Test
    void testObjectType() {
        def instance = new VoTableBuilder().votable {
            model(spec: dsSpec)
            object(type: "$org") {
                value(role: "name", value:"OrgName")
                value(role: "address", value:"An Address")
            }
        }

        def expected = new VotableInstance()
        expected << new ModelInstance(spec: dsSpec)
        def obj = new ObjectInstance(type: "$org")
        obj.attributes << new ValueInstance(role: "ds:party.Party.name", value:"OrgName")
        obj.attributes << new ValueInstance(role: "${org}.address", value:"An Address")
        expected.objectTypes << obj

        assert instance == expected
    }

    public static String preamble(String nested) {
        """<?xml version="1.0" encoding="UTF-8"?>
           <VOTABLE xmlns="http://www.ivoa.net/xml/VOTable/v1.4c"
                      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
              <RESOURCE>
                <GROUP>
                  <VODML>
                    <TYPE>vo-dml:Model</TYPE>
                  </VODML>
                  <PARAM name="name" datatype="char" arraysize="2" value="ds">
                      <VODML>
                        <ROLE>vo-dml:Model.name</ROLE>
                      </VODML>
                  </PARAM>
                  <PARAM name="version" datatype="char" arraysize="3" value="0.x">
                      <VODML>
                        <ROLE>vo-dml:Model.version</ROLE>
                      </VODML>
                  </PARAM>
                  <PARAM name="url" datatype="char" arraysize="36" value="http://some/where/dataset.vo-dml.xml">
                      <VODML>
                        <ROLE>vo-dml:Model.url</ROLE>
                      </VODML>
                  </PARAM>
                </GROUP>
                ${nested}
              </RESOURCE>
           </VOTABLE>"""
    }

    public static String datatype() {
        '''<?xml version="1.0" encoding="UTF-8"?>
           <VOTABLE xmlns="http://www.ivoa.net/xml/VOTable/v1.3-4c"
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
