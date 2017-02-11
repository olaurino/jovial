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
                instance(role: "name", value: "3C273")
                instance(role: "description", value: "A Quasar")
                instance(role: "position") {
                    instance(role: "coord", type: "stc:stctypes.RealDoublet") {
                        instance(role: "d1", value: 187.2792)
                        instance(role: "d2", value: 2.0525)
                    }
                }
                instance(role: "objectClass", value: "BLAZAR")
                instance(role: "spectralClass", value: "Sy1")
                instance(role: "redshift", value: 0.158)
                instance(role: "varAmpl", value: Double.NaN)
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
                instance(role: "observationID", value: "obsid.2015.73")
                instance(role: "target", type: "ds:experiment.AstroTarget") {
                    instance(role: "name", value: "3C273")
                    instance(role: "description", value: "A Quasar")
                    instance(role: "position") {
                        instance(role: "coord", type: "stc:stctypes.RealDoublet") {
                            instance(role: "d1", value: 187.2792)
                            instance(role: "d2", value: 2.0525)
                        }
                    }
                    instance(role: "objectClass", value: "BLAZAR")
                    instance(role: "spectralClass", value: "Sy1")
                    instance(role: "redshift", value: 0.158)
                    instance(role: "varAmpl", value: Double.NaN)
                }
                instance(role: "obsConfig") {
                    instance(role: "bandpass", value: "optical")
                    instance(role: "datasource", value: "survey")
                    instance(role: "instrument") {
                        instance(role: "name", value: "ACIS")
                    }
                    instance(role: "facility") {
                        reference(role: "party", idref: "ACME")
                    }
                }
                instance(role: "proposal") {
                    instance(role: "identifier", value: "PROPOSAL/756/2014.06")
                }
                instance(role: "result", type: "ds:experiment.ObsDataset") {
                    instance(role: "dataProductType", value: "ds:dataset.DataProductType.CUBE")
                    instance(role: "dataProductSubtype", value: "MySubtype")
                    instance(role: "calibLevel", value: 0)
                    instance(role: "characterisation")
                    instance(role: "derived") {
                        instance(role: "derivedElement", type:"ds:experiment.DerivedScalar") {
                            instance(role: "name", value: "SNR")
                            instance(role: "value", type:"ivoa:quantity.RealQuantity", value: 1.25)
                        }
                    }
                    instance(role: "dataID") {
                        instance(role: "title", value: "datasetTitle")
                        instance(role: "datasetID", value: "ivo://some/uri")
                        instance(role: "creatorDID", value: "me://some/other/uri")
                        instance(role: "version", value: "DR3")
                        instance(role: "date", value: "20160422T11:55:30")
                        instance(role: "creationType", value: "ds:dataset.CreationType.ARCHIVAL")
                        instance(role: "collection") {
                            instance(role: "name", value: "Data Release 3")
                        }
                        instance(role: "contributor") {
                            instance(role: "acknowledgment", value: "Project Manager")
                            reference(role: "party", idref: "BILL")
                        }
                            instance(role: "contributor") {
                                instance(role: "acknowledgment", value: "Bought the donuts!")
                                reference(role: "party", idref: "TOM")
                            }
                        instance(role: "creator") {
                            reference(role: "party", idref: "ACME")
                        }
                    }
                    instance(role: "curation") {
                        instance(role: "publisherDID", value: "me://some/other/uri")
                        instance(role: "version", value: "DR3")
                        instance(role: "releaseDate", value: "20160422T11:55:30")
                        instance(role: "rights", value: "ds:dataset.RightsType.PUBLIC")
                        instance(role: "contact") {
                            reference(role: "party", idref: "BILL")
                        }
                        instance(role: "publisher") {
                            instance(role: "publisherID", value: "ivo://acme.org")
                            reference(role: "party", idref: "ACME")
                        }
                        instance(role: "reference", type: "ds:dataset.Publication") {
                            instance(role: "refCode", value: "ApJ12345")
                        }
                        instance(role: "reference", type: "ds:dataset.Publication") {
                            instance(role: "refCode", value: "ApJ6789")
                        }
                    }
                }
            }
            instance(id: "ACME", type: "ds:party.Organization") {
                instance(role: "name", value: "ACME edu")
                instance(role: "address", value: "565 N Clinton Drive, Milwaukee, WI")
                instance(role: "phone", value: "555-012-3456")
                instance(role: "email", value: "helpdesk@acme.org")
                instance(role: "logo", value: "http://acme.org/stunning.png")
            }
            instance(id: "BILL", type: "ds:party.Individual") {
                instance(role: "name", value: "William E. Coyote")
                instance(role: "address", value: "565 N Clinton Drive, Milwaukee, WI")
                instance(role: "phone", value: "555-654-3210")
                instance(role: "email", value: "bill@acme.org")
            }
            instance(id: "TOM", type: "ds:party.Individual") {
                instance(role: "name", value: "Tom Ray")
                instance(role: "address", value: "565 N Clinton Drive, Milwaukee, WI")
                instance(role: "phone", value: "555-999-5555")
                instance(role: "email", value: "bill@acme.org")
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
                instance(role: "name", value:"OrgName")
                instance(role: "address", value:"An Address")
            }
        }

        def expected = new DataModelInstance()
        expected << new ModelImportInstance(spec: dsSpec)
        def obj = new ObjectInstance(type: "$org")
        def name = new ObjectInstance(role: "ds:party.Party.name", value:"OrgName")
        name.apply()
        def address = new ObjectInstance(role: "${org}.address", value:"An Address")
        address.apply()
        obj << name
        obj << address
        expected.objectTypes << obj

        assert instance == expected

        assert new ObjectInstance(role: "foo") != new ObjectInstance(role: "bar")
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
