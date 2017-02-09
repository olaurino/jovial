/*
 * #%L
 * jovial
 * %%
 * Copyright (C) 2016 Smithsonian Astrophysical Observatory
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Smithsonian Astrophysical Observatory nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package cfa.vo.vodml.io

import cfa.vo.vodml.instance.AttributeInstance
import cfa.vo.vodml.instance.DataModelInstance
import cfa.vo.vodml.instance.ModelImportInstance
import cfa.vo.vodml.instance.ObjectInstance
import cfa.vo.vodml.io.votable.VotableWriter
import cfa.vo.vodml.metamodel.Model
import cfa.vo.vodml.utils.XmlUtils
import org.junit.Before
import org.junit.Test

class VoTableBuilderTest {
    def writer = new VotableWriter()
    ByteArrayOutputStream os = new ByteArrayOutputStream()
    Model dsSpec;

    Model stcSpec;
    Model charSpec;
    Model ivoaSpec;
    String org = "ds:party.Organization"

    @Before
    void setUp() {
        def reader = new VodmlReader()
        dsSpec = reader.read(getClass().getResource("/DatasetMetadata-1.0.vo-dml.xml").openStream())
        ivoaSpec = reader.read(getClass().getResource("/ivoa.vo-dml.xml").openStream())
        charSpec = reader.read(getClass().getResource("/char.vo-dml.xml").openStream())
        stcSpec = reader.read(getClass().getResource("/stc2.vo-dml.xml").openStream())
    }

    @Test
    void testPreamble() {
        def builder = new VoTableBuilder()

        DataModelInstance instance = builder.votable {
            model(spec: dsSpec, vodmlURL: "http://some/where/dataset.vo-dml.xml")
        }

        writer.write(instance, os)
        String actual = os.toString("UTF-8")
        String expected = preamble("")
        XmlUtils.testXml(expected, actual)
    }

    @Test
    void testTarget() {
        DataModelInstance instance = new VoTableBuilder().votable {
            model(spec: stcSpec, vodmlURL: "https://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/STC2/prototype/STCPrototype-2.0.vo-dml.xml")
            model(spec: dsSpec, vodmlURL: "http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/ds/DatasetMetadata-1.0.vo-dml.xml")
            instance(type: "ds:experiment.AstroTarget") {
                attribute(role: "name", value: "3C273")
                attribute(role: "description", value: "A Quasar")
                attribute(role: "position") {
                    attribute(role: "coord", type: "stc:stctypes.RealDoublet") {
                        attribute(role: "d1", value: 187.2792)
                        attribute(role: "d2", value: 2.0525)
                    }
                }
                attribute(role: "objectClass", value: "BLAZAR")
                attribute(role: "spectralClass", value: "Sy1")
                attribute(role: "redshift", value: 0.158)
                attribute(role: "varAmpl", value: Double.NaN)
            }
        }

        def votable = """<?xml version="1.0" encoding="UTF-8"?>
<VOTABLE xmlns="http://www.ivoa.net/xml/VOTable/v1.4" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <GROUP>
    <VODML>
      <TYPE>vodml-map:Model</TYPE>
    </VODML>
    <PARAM datatype="char" arraysize="92" name="url" value="http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/ds/DatasetMetadata-1.0.vo-dml.xml">
      <VODML>
        <ROLE>vodml-map:Model.url</ROLE>
        <TYPE>ivoa:anyURI</TYPE>
      </VODML>
    </PARAM>
    <PARAM datatype="char" arraysize="2" name="name" value="ds">
      <VODML>
        <ROLE>vodml-map:Model.name</ROLE>
        <TYPE>ivoa:string</TYPE>
      </VODML>
    </PARAM>
  </GROUP>
  <!--End ObjectType role: {No Role} type: vodml-map:Model-->
  <RESOURCE>
    <GROUP>
      <VODML>
        <TYPE>ds:experiment.AstroTarget</TYPE>
      </VODML>
      <PARAM datatype="char" arraysize="5" name="name" value="3C273">
        <VODML>
          <ROLE>ds:experiment.BaseTarget.name</ROLE>
          <TYPE>ivoa:string</TYPE>
        </VODML>
      </PARAM>
      <PARAM datatype="char" arraysize="8" name="description" value="A Quasar">
        <VODML>
          <ROLE>ds:experiment.BaseTarget.description</ROLE>
          <TYPE>ivoa:string</TYPE>
        </VODML>
      </PARAM>
      <PARAM datatype="char" arraysize="6" name="objectClass" value="BLAZAR">
        <VODML>
          <ROLE>ds:experiment.AstroTarget.objectClass</ROLE>
          <TYPE>ivoa:string</TYPE>
        </VODML>
      </PARAM>
      <PARAM datatype="char" arraysize="3" name="spectralClass" value="Sy1">
        <VODML>
          <ROLE>ds:experiment.AstroTarget.spectralClass</ROLE>
          <TYPE>ivoa:string</TYPE>
        </VODML>
      </PARAM>
      <PARAM datatype="float" arraysize="1" name="redshift" value="0.158">
        <VODML>
          <ROLE>ds:experiment.AstroTarget.redshift</ROLE>
          <TYPE>ivoa:real</TYPE>
        </VODML>
      </PARAM>
      <PARAM datatype="float" arraysize="1" name="varAmpl" value="NaN">
        <VODML>
          <ROLE>ds:experiment.AstroTarget.varAmpl</ROLE>
          <TYPE>ivoa:real</TYPE>
        </VODML>
      </PARAM>
    </GROUP>
    <!--End ObjectType role: {No Role} type: ds:experiment.AstroTarget-->
  </RESOURCE>
</VOTABLE>
"""

        writer.write(instance, System.out)
    }

    @Test
    void testDatasetInstance() {
        DataModelInstance instance = new VoTableBuilder().votable {
            model(spec: dsSpec, vodmlURL: "http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/ds/DatasetMetadata-1.0.vo-dml.xml")
            model(spec: stcSpec, vodmlURL: "https://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/STC2/prototype/STCPrototype-2.0.vo-dml.xml")
            model(spec: charSpec, vodmlURL: "http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/characterization/Characterization.vo-dml.xml")
            model(spec: ivoaSpec, vodmlURL: "https://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/ivoa/IVOA.vo-dml.xml")
            instance(type: "ds:experiment.Observation") {
                attribute(role: "observationID", value:"obsid.2015.73")
                composition(role: "target") {
                    instance(type: "ds:experiment.AstroTarget") {
                        attribute(role: "name", value: "3C273")
                        attribute(role: "description", value: "A Quasar")
                        attribute(role: "position") {
                            attribute(role: "coord", type: "stc:stctypes.RealDoublet") {
                                attribute(role: "d1", value: 187.2792)
                                attribute(role: "d2", value: 2.0525)
                            }
                        }
                        attribute(role: "objectClass", value: "BLAZAR")
                        attribute(role: "spectralClass", value: "Sy1")
                        attribute(role: "redshift", value: 0.158)
                        attribute(role: "varAmpl", value: Double.NaN)
                    }
                }
                composition(role: "obsConfig") {
                    instance(type: "ds:experiment.ObsConfig") {
                        attribute(role: "bandpass", value: "optical")
                        attribute(role: "datasource", value: "survey")
                        composition(role: "instrument") {
                            instance(type: "ds:experiment.Instrument") {
                                attribute(role: "name", value: "ACIS")
                            }
                        }
                        composition(role: "facility") {
                            instance(type: "ds:experiment.Facility") {
                                reference(role: "party", value: "ACME")
                            }
                        }
                    }
                }
                composition(role: "proposal") {
                    instance(type: "ds:experiment.Proposal") {
                        attribute(role: "identifier", value: "PROPOSAL/756/2014.06")
                    }
                }
                composition(role: "result") {
                    instance(type: "ds:experiment.ObsDataset") {
                        attribute(role: "dataProductType", value: "ds:dataset.DataProductType.CUBE")
                        attribute(role: "dataProductSubtype", value: "MySubtype")
                        attribute(role: "calibLevel", value: 0)
                        composition(role: "characterisation") {
                            instance(type: "ds:experiment.Characterisation")
                        }
                        composition(role: "derived") {
                            instance(type: "ds:experiment.Derived") {
                                composition(role: "derivedElement") {
                                    instance(type: "ds:experiment.DerivedScalar") {
                                        attribute(role: "name", value: "SNR")
                                        attribute(role: "value", value: 1.25)
                                    }
                                }
                            }
                        }
                        composition(role: "dataID") {
                            instance(type: "ds:dataset.DataID") {
                                attribute(role: "title", value: "datasetTitle")
                                attribute(role: "datasetID", value: "ivo://some/uri")
                                attribute(role: "creatorDID", value: "me://some/other/uri")
                                attribute(role: "version", value: "DR3")
                                attribute(role: "date", value: "20160422T11:55:30")
                                attribute(role: "creationType", value: "ds:dataset.CreationType.ARCHIVAL")
                                composition(role: "collection") {
                                    instance(type: "ds:dataset.Collection") {
                                        attribute(role: "name", value: "Data Release 3")
                                    }
                                }
                                composition(role: "contributor") {
                                    instance(type: "ds:dataset.Contributor") {
                                        attribute(role: "acknowledgment", value: "Project Manager")
                                        reference(role: "party", value: "BILL")
                                    }
                                    instance(type: "ds:dataset.Contributor") {
                                        attribute(role: "acknowledgment", value: "Bought the donuts!")
                                        reference(role: "party", value: "TOM")
                                    }
                                }
                                composition(role: "creator") {
                                    instance(type: "ds:dataset.Creator") {
                                        reference(role: "party", value: "ACME")
                                    }
                                }
                            }
                        }
                        composition(role: "curation") {
                            instance(type: "ds:dataset.Curation") {
                                attribute(role: "publisherDID", value: "me://some/other/uri")
                                attribute(role: "version", value: "DR3")
                                attribute(role: "releaseDate", value: "20160422T11:55:30")
                                attribute(role: "rights", value: "ds:dataset.RightsType.PUBLIC")
                                composition(role: "contact") {
                                    instance(type: "ds:dataset.Contact") {
                                        reference(role: "party", value: "BILL")
                                    }
                                }
                                composition(role: "publisher") {
                                    instance(type: "ds:dataset.Publisher") {
                                        attribute(role: "publisherID", value: "ivo://acme.org")
                                        reference(role: "party", value: "ACME")
                                    }
                                }
                                composition(role: "reference") {
                                    instance(type: "ds:dataset.Publication") {
                                        attribute(role: "refCode", value: "ApJ12345")
                                    }
                                    instance(type: "ds:dataset.Publication") {
                                        attribute(role: "refCode", value: "ApJ6789")
                                    }
                                }
                            }
                        }
                    }
                }
            }
            instance(id: "ACME", type: "ds:party.Organization") {
                attribute(role: "name", value: "ACME edu")
                attribute(role: "address", value: "565 N Clinton Drive, Milwaukee, WI")
                attribute(role: "phone", value: "555-012-3456")
                attribute(role: "email", value: "helpdesk@acme.org")
                attribute(role: "logo", value: "http://acme.org/stunning.png")
            }
            instance(id: "BILL", type: "ds:party.Individual") {
                attribute(role: "name", value: "William E. Coyote")
                attribute(role: "address", value: "565 N Clinton Drive, Milwaukee, WI")
                attribute(role: "phone", value: "555-654-3210")
                attribute(role: "email", value: "bill@acme.org")
            }
            instance(id: "TOM", type: "ds:party.Individual") {
                attribute(role: "name", value: "Tom Ray")
                attribute(role: "address", value: "565 N Clinton Drive, Milwaukee, WI")
                attribute(role: "phone", value: "555-999-5555")
                attribute(role: "email", value: "bill@acme.org")
            }
            instance(id: "COORDSYS", type: "stc:coordsystem.AstroCoordSystem")
        }

        writer.write(instance, System.out)
    }

    @Test
    void testObjectType() {
        def instance = new VoTableBuilder().votable {
            model(spec: dsSpec)
            instance(type: "$org") {
                attribute(role: "name", value:"OrgName")
                attribute(role: "address", value:"An Address")
            }
        }

        def expected = new DataModelInstance()
        expected << new ModelImportInstance(spec: dsSpec)
        def obj = new ObjectInstance(type: "$org")
        obj.attributes << new AttributeInstance(role: "ds:party.Party.name", value:"OrgName")
        obj.attributes << new AttributeInstance(role: "${org}.address", value:"An Address")
        expected.objectTypes << obj

        assert instance == expected

        assert new AttributeInstance(role: "foo") != new AttributeInstance(role: "bar")
    }

    public static String preamble(String nested) {
        """<?xml version="1.0" encoding="UTF-8"?>
<VOTABLE xmlns="http://www.ivoa.net/xml/VOTable/v1.4" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <VODML>
    <MODEL>
      <NAME>ds</NAME>
      <URL>http://some/where/dataset.vo-dml.xml</URL>
    </MODEL>
  </VODML>
</VOTABLE>
"""
    }

    public static String datatype() {
        '''<?xml version="1.0" encoding="UTF-8"?>
           <VOTABLE xmlns="http://www.ivoa.net/xml/VOTable/v1.4"
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
