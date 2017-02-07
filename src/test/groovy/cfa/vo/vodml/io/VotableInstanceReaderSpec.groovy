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
import cfa.vo.vodml.io.instance.InstanceFactory
import cfa.vo.vodml.io.instance.InstanceReader
import cfa.vo.vodml.io.instance.InstanceWriter
import cfa.vo.vodml.utils.XmlUtils
import spock.lang.Specification
import spock.lang.Unroll

class VotableInstanceReaderSpec extends Specification {
    static private ivoaSpec
    static private vodmlSpec
    static private photSpec
    static private sampleSpec
    static private associationSpec

    private final static ivoaURL = "http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/ivoa/IVOA.vo-dml.xml"
    private final static vodmlURL = "http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/vodml-map/vodml-map.vo-dml.xml"
    private final static filterURL = "http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/sample/filter/Filter.vo-dml.xml"
    private final static sampleURL = "http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/sample/sample/Sample.vo-dml.xml"
    private final static associationURL = "http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/sample/Association/Association.vo-dml.xml"

    InstanceReader votableInstanceReader = InstanceFactory.readerFor("votable")
    InstanceReader vodmlInstanceReader = InstanceFactory.readerFor("vodml")
    InstanceWriter vodmlInstanceWriter = InstanceFactory.writerFor("vodml")
    InstanceWriter votableAltInstanceWriter = InstanceFactory.writerFor("votableAlt")

    def setupSpec() {
        def reader = new VodmlReader()
        ivoaSpec = reader.read(getStream(ivoaURL))
        vodmlSpec = reader.read(getStream(vodmlURL))
        photSpec = reader.read(getStream(filterURL))
        sampleSpec = reader.read(getStream(sampleURL))
        associationSpec = reader.read(getStream(associationURL))
    }

    def "Models are different"() {
        given: "two votables with different model declarations"
        DataModelInstance one = votableInstanceReader.read(modelOne)
        DataModelInstance two = votableInstanceReader.read(modelTwo)

        expect: "they are not the same instance"
        !one.equals(two)
    }

    def "Model are the same, one with arraysize *"() {
        given: "two votables with same model declarations but * in arraysize"
        DataModelInstance one = votableInstanceReader.read(modelOne)
        DataModelInstance two = votableInstanceReader.read(modelOneStar)

        expect: "they are the same instance"
        one.equals(two)
    }

    @Unroll
    def "test mapping with /mapping-examples/#name .votable.xml"(name) {
        given: "$name standard instance and votable representation"
        def standard = getExampleText("/mapping-examples/${name}.votable.xml.vo-dml.xml")
        def dslInstance = this."${name}Instance"

        when: "instances are parsed"
        DataModelInstance standardInstance = vodmlInstanceReader.read(standard)

        and: "instances are serialized to vodmli"
        def baos = new ByteArrayOutputStream()
        def encoding = "UTF-8"
        votableAltInstanceWriter.write(dslInstance, System.out)
        baos.reset()
        vodmlInstanceWriter.write(dslInstance, baos)
        def serializedDslInstance = baos.toString(encoding)
        baos.reset()
        vodmlInstanceWriter.write(standardInstance, baos)
        def serializedStandardInstance = baos.toString(encoding)
        baos.close()

        then: "standard representation round-tripping"
        XmlUtils.testVodmlInstanceXml(standard, serializedStandardInstance)

//        and: "standard representation of votableAlt instance is equal to standard"
//        XmlUtils.assertVotableAltEqual(standard, serializedAltVotableInstance)

        and: "standard representation of dsl instance is equal to standard"
        XmlUtils.testVodmlInstanceXml(standard, serializedDslInstance)

        where:
        name         | _
        "test1"      | _
        "test2"      | _
        "test3"      | _
        "test4"      | _
        "test5"      | _
    }

    def test1Instance = new VoTableBuilder().votable {
        model(spec: vodmlSpec, vodmlURL: getExampleURL(vodmlURL))
        model(spec: ivoaSpec, vodmlURL: getExampleURL(ivoaURL))
        model(spec: photSpec, vodmlURL: getExampleURL(filterURL), identifier: "ivo://ivoa.org/dm/sample/Filter/1.9")
    }

    def test2Instance = new VoTableBuilder().votable {
        model(spec: vodmlSpec, vodmlURL: getExampleURL(vodmlURL))
        model(spec: ivoaSpec, vodmlURL: getExampleURL(ivoaURL))
        model(spec: photSpec, vodmlURL: getExampleURL(filterURL), identifier: "ivo://ivoa.org/dm/sample/Filter/1.9")
        instance(type: "filter:PhotometryFilter") {
            literal(role: "name", value: "J")
            literal(role: "bandName", value: "2mass:J")
            attribute(role: "spectralLocation") {
                instance() {
                    literal(role: "unit", value: "nm")
                    literal(role: "value", value: 1235.0)
                }
            }
        }
        instance(type: "filter:PhotometryFilter") {
            literal(role: "name", value: "H")
            literal(role: "bandName", value: "2mass:H")
            attribute(role: "spectralLocation") {
                literal(role: "unit", value: "nm")
                literal(role: "value", value: 1662.0)
            }
        }
    }

    def test3Instance = new VoTableBuilder().votable {
        model(spec: vodmlSpec, vodmlURL: getExampleURL(vodmlURL))
        model(spec: ivoaSpec, vodmlURL: getExampleURL(ivoaURL))
        model(spec: photSpec, vodmlURL: getExampleURL(filterURL), identifier: "ivo://ivoa.org/dm/sample/Filter/1.9")
        instance(type: "filter:PhotometricSystem") {
            literal(role: "description", value: "The 2MASS phtometric system, consisting of 3 bands")
            composition(role: "filter:PhotometricSystem.photometryFilter") {
                instance() {
                    literal(role: "name", value: "J")
                    literal(role: "bandName", value: "2mass:J")
                    attribute(role: "spectralLocation") {
                        literal(role: "unit", value: "nm")
                        literal(role: "value", value: 1235.0)
                    }
                }
                instance() {
                    literal(role: "name", value: "H")
                    literal(role: "bandName", value: "2mass:H")
                    attribute (role: "spectralLocation") {
                        literal(role: "unit", value: "nm")
                        literal(role: "value", value: 1662.0)
                    }
                }
            }
        }
    }

    def test4Instance = new VoTableBuilder().votable {
        model(spec: vodmlSpec, vodmlURL: getExampleURL(vodmlURL))
        model(spec: ivoaSpec, vodmlURL: getExampleURL(ivoaURL))
        model(spec: photSpec, vodmlURL: getExampleURL(filterURL), identifier: "ivo://ivoa.org/dm/sample/Filter/1.9")
        model(spec: sampleSpec, vodmlURL: getExampleURL(sampleURL))
        instance(type: "sample:catalog.SkyCoordinateFrame", id:"_icrs") {
            literal(role: "name", value: "ICRS")
        }
        instance(type: "filter:PhotometryFilter") {
            literal(role: "name", value: "2mass:J")
        }
        instance(type: "filter:PhotometryFilter") {
            literal(role: "name", value: "2mass:H")
        }
        instance(type: "filter:PhotometryFilter") {
            literal(role: "name", value: "2mass:K")
        }
        table() {
            instance(type: "sample:catalog.Source") {
                column(role: "name", value: "_designation")
                attribute(role: "position", type: "sample:catalog.SkyCoordinate") {
                    column(role: "longitude", value: "_ra")
                    column(role: "latitude", value: "_dec")
                    reference(role: "frame", value: "_icrs")
                }
            }
        }
    }

    def test5Instance = new VoTableBuilder().votable {
        model(spec: vodmlSpec, vodmlURL: getExampleURL(vodmlURL))
        model(spec: ivoaSpec, vodmlURL: getExampleURL(ivoaURL))
        model(spec: associationSpec, vodmlURL: getExampleURL(associationURL))
        table() {
            instance(type: "Association:base.Flight", id: "FLIGHT") {
                column(role: "Number", value: "_FLIGHT_ID")
                column(role: "Destination", value: "_DEST")
            }
        }
        table() {
            instance(type: "Association:base.Passenger") {
                column(role: "FullName", value: "_NAME")
                column(role: "SerialNumber", value: "_SERIAL")
            }
        }
    }

    def modelOne = """<?xml version="1.0" encoding="UTF-8"?>
           <VOTABLE xmlns="http://www.ivoa.net/xml/VOTable/v1.3_vodml"
                      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
              <GROUP>
                <VODML>
                  <TYPE>vodml-map:Model</TYPE>
                </VODML>
                <PARAM name="url" datatype="char" arraysize="36" value="http://some/where/dataset.vo-dml.xml">
                    <VODML>
                      <ROLE>vodml-map:Model.url</ROLE>
                      <TYPE>ivoa:anyURI</TYPE>
                    </VODML>
                </PARAM>
                <PARAM name="name" datatype="char" arraysize="2" value="ds">
                    <VODML>
                      <ROLE>vodml-map:Model.identifier</ROLE>
                      <TYPE>ivoa:string</TYPE>
                    </VODML>
                </PARAM>
              </GROUP>
              <RESOURCE>
              </RESOURCE>
           </VOTABLE>"""

    def modelTwo = """<?xml version="1.0" encoding="UTF-8"?>
           <VOTABLE xmlns="http://www.ivoa.net/xml/VOTable/v1.3_vodml"
                      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
              <GROUP>
                <VODML>
                  <TYPE>vodml-map:Model</TYPE>
                </VODML>
                <PARAM name="url" datatype="char" arraysize="*" value="http://some/where/else.xml">
                    <VODML>
                      <ROLE>vodml-map:Model.url</ROLE>
                      <TYPE>ivoa:anyURI</TYPE>
                    </VODML>
                </PARAM>
                <PARAM name="name" datatype="char" arraysize="*" value="name">
                    <VODML>
                      <ROLE>vodml-map:Model.identifier</ROLE>
                      <TYPE>ivoa:string</TYPE>
                    </VODML>
                </PARAM>
              </GROUP>
              <RESOURCE>
              </RESOURCE>
           </VOTABLE>"""

    def modelOneStar = """<?xml version="1.0" encoding="UTF-8"?>
           <VOTABLE xmlns="http://www.ivoa.net/xml/VOTable/v1.3_vodml"
                      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
              <GROUP>
                <VODML>
                  <TYPE>vodml-map:Model</TYPE>
                </VODML>
                <PARAM name="url" datatype="char" arraysize="*" value="http://some/where/dataset.vo-dml.xml">
                    <VODML>
                      <ROLE>vodml-map:Model.url</ROLE>
                      <TYPE>ivoa:anyURI</TYPE>
                    </VODML>
                </PARAM>
                <PARAM name="name" datatype="char" arraysize="*" value="ds">
                    <VODML>
                      <ROLE>vodml-map:Model.identifier</ROLE>
                      <TYPE>ivoa:string</TYPE>
                    </VODML>
                </PARAM>
              </GROUP>
              <RESOURCE>
              </RESOURCE>
           </VOTABLE>"""

    private static getExampleText(String location) {
        return VotableInstanceReaderSpec.class.getResourceAsStream(location).text
    }

    private static getStream(location) {
        return (new URL(location)).openStream()
    }

    // TODO Eventually, I don't want to rely on external resources, so this method
    // could translate an actual URL to a local one.
    private static getExampleURL(location) {
        return location
    }
}
