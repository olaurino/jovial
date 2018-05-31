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

class DataModelInstanceBuilderTest {
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
        def builder = new DataModelInstanceBuilder()

        DataModelInstance instance = builder.dmInstance {
            model(spec: dsSpec, vodmlURL: "http://some/where/dataset.vo-dml.xml")
        }

        writer.write(instance, os)
        String actual = os.toString("UTF-8")
        String expected = preamble("")
        XmlUtils.testXml(expected, actual)
    }

    @Test
    void testObjectType() {
        def instance = new DataModelInstanceBuilder().dmInstance {
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
  <RESOURCE ID="EMPTY"/>
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
