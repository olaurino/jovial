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

import cfa.vo.vodml.instance.VotableInstance
import org.custommonkey.xmlunit.XMLAssert
import org.custommonkey.xmlunit.XMLUnit
import spock.lang.Specification
import spock.lang.Shared


class FocusSpec extends Specification {
    static private ivoaSpec
    static private vodmlSpec
    static private photSpec

    private final static ivoaURL = "http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/ivoa/IVOA.vo-dml.xml"
    private final static vodmlURL = "http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/vodml-map/vodml-map.vo-dml.xml"
    private final static photURL = "http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/photdm-alt/PhotDM-alt.vo-dml.xml"

    def os = new ByteArrayOutputStream()

    def setupSpec() {
        XMLUnit.setIgnoreWhitespace(true)
        XMLUnit.setIgnoreComments(true)
        def reader = new VodmlReader()
        ivoaSpec = reader.read(getStream(ivoaURL))
        vodmlSpec = reader.read(getStream(vodmlURL))
        photSpec = reader.read(getStream(photURL))
    }

    def "test 1 - vodmlmap:Model"() {
        given: "a preamble instance and its expected serialization"
            def expected = test1Serialization
            def instance = test1Instance
        when: "the instance is serialized to a string"
            instance.toXml(os)
            def actual = os.toString("UTF-8")
        then: "expected and actual serializations are equivalent"
            XMLAssert.assertXMLEqual(expected, actual)
    }

    def "test 2 - Standalone instances from Photdm-alt data model"() {
        given: "a standalone instance of an objectType and its expected serialization"
            def expected = test2Serialization
            def instance = test2Instance
        when: "the instance is serialized to a string"
            instance.toXml(os)
            def actual = os.toString("UTF-8")
        then: "expected and actual serializations are equivalent"
            XMLAssert.assertXMLEqual(expected, actual)
    }

    def test1Instance = new VoTableBuilder().votable {
        model(spec: vodmlSpec, vodmlURL: getExampleString(vodmlURL))
        model(spec: ivoaSpec, vodmlURL: getExampleString(ivoaURL))
        model(spec: photSpec, vodmlURL: getExampleString(photURL), identifier: "ivo://ivoa.org/dm/PhotDM-alt/1.9")
    }

    def test2Instance = new VoTableBuilder().votable {
        model(spec: vodmlSpec, vodmlURL: getExampleString(vodmlURL))
        model(spec: ivoaSpec, vodmlURL: getExampleString(ivoaURL))
        model(spec: photSpec, vodmlURL: getExampleString(photURL), identifier: "ivo://ivoa.org/dm/PhotDM-alt/1.9")
    }

    def test1Serialization = """<?xml version="1.0" encoding="UTF-8"?>
<!--
Test for vodml-map:Model
 -->
<VOTABLE xmlns="http://www.ivoa.net/xml/VOTable/v1.3_vodml">
<!--
Declaration of VO-DML modes that are used in this annotated VOTable.
See \$7.1.
-->
\t<GROUP>
\t\t<VODML><TYPE>vodml-map:Model</TYPE></VODML>
\t\t<PARAM name="url" datatype="char" arraysize="*"
\t\t\tvalue="http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/vodml-map/vodml-map.vo-dml.xml">
\t\t\t<VODML><ROLE>vodml-map:Model.url</ROLE><TYPE>ivoa:anyURI</TYPE></VODML>
\t\t</PARAM>
\t\t<PARAM name="name" datatype="char" arraysize="*" value="vodml-map">
\t\t\t<VODML><ROLE>vodml-map:Model.name</ROLE><TYPE>ivoa:string</TYPE></VODML>
\t\t</PARAM>
\t</GROUP>
\t
\t<GROUP>
\t\t<VODML><TYPE>vodml-map:Model</TYPE></VODML>
\t\t
\t\t<PARAM name="url" datatype="char" arraysize="*"
\t\t\tvalue="http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/ivoa/IVOA.vo-dml.xml">
\t\t\t<VODML><ROLE>vodml-map:Model.url</ROLE><TYPE>ivoa:anyURI</TYPE></VODML>
\t\t</PARAM>
\t\t<PARAM name="name" datatype="char" arraysize="*" value="ivoa">
\t\t\t<VODML><ROLE>vodml-map:Model.name</ROLE><TYPE>ivoa:string</TYPE></VODML>
\t\t</PARAM>
\t\t
\t</GROUP>
\t
\t<GROUP>
\t\t<VODML><TYPE>vodml-map:Model</TYPE></VODML>
\t\t<PARAM name="url" datatype="char" arraysize="*"
\t\t\tvalue="http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/photdm-alt/PhotDM-alt.vo-dml.xml">
\t\t\t<VODML><ROLE>vodml-map:Model.url</ROLE><TYPE>ivoa:anyURI</TYPE></VODML>
\t\t</PARAM>
\t\t
\t\t<PARAM name="identifier" datatype="char" arraysize="*"
\t\t\tvalue="ivo://ivoa.org/dm/PhotDM-alt/1.9">
\t\t\t<VODML><ROLE>vodml-map:Model.identifier</ROLE><TYPE>ivoa:anyURI</TYPE></VODML>
\t\t</PARAM>
\t\t
\t\t<PARAM name="name" datatype="char" arraysize="*" value="photdm-alt">
\t\t\t<VODML><ROLE>vodml-map:Model.name</ROLE><TYPE>ivoa:string</TYPE></VODML>
\t\t</PARAM>
\t</GROUP>
\t
\t<RESOURCE/>
\t
\t
</VOTABLE>
"""

    def test2Serialization = """<?xml version="1.0" encoding="UTF-8"?>
<!--
Standalore instances from Photdm-alt data model.
Secial features:
- standalone instances of objecttype
- ivoa:quantity.RealQuantity serializing to PARAM (implicit use of PARAM@unit) and explicitly to GROUP
-->
<VOTABLE xmlns="http://www.ivoa.net/xml/VOTable/v1.3_vodml">
<!--
Declaration of VO-DML modes that are used in this annotated VOTable.
See \$7.1.
-->
<!-- Import vodml-map model -->
<GROUP>
<VODML><TYPE>vodml-map:Model</TYPE></VODML>

<PARAM name="url" datatype="char" arraysize="*"
value="http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/vodml-map/vodml-map.vo-dml.xml">
<VODML><ROLE>vodml-map:Model.url</ROLE><TYPE>ivoa:anyURI</TYPE></VODML>
</PARAM>

<PARAM name="name" datatype="char" arraysize="*" value="vodml-map">
<VODML><ROLE>vodml-map:Model.name</ROLE><TYPE>ivoa:string</TYPE></VODML>
</PARAM>

</GROUP>

<!-- Import ivoa model -->
<GROUP>
<VODML><TYPE>vodml-map:Model</TYPE></VODML>

<PARAM name="url" datatype="char" arraysize="*"
value="http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/ivoa/IVOA.vo-dml.xml">
<VODML><ROLE>vodml-map:Model.url</ROLE><TYPE>ivoa:anyURI</TYPE></VODML>
</PARAM>

<PARAM name="name" datatype="char" arraysize="*" value="ivoa">
<VODML><ROLE>vodml-map:Model.name</ROLE><TYPE>ivoa:string</TYPE></VODML>
</PARAM>

</GROUP>


<!-- Import photdm-alt model -->
<GROUP>
<PARAM name="url" datatype="char" arraysize="*"
value="http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/photdm-alt/PhotDM-alt.vo-dml.xml">
<VODML><ROLE>vodml-map:Model.url</ROLE><TYPE>ivoa:anyURI</TYPE></VODML>
</PARAM>

<PARAM name="identifier" datatype="char" arraysize="*"
value="ivo://ivoa.org/dm/PhotDM-alt/1.9">
<VODML><ROLE>vodml-map:Model.identifier</ROLE><TYPE>ivoa:anyURI</TYPE></VODML>
</PARAM>

<VODML><TYPE>vodml-map:Model</TYPE></VODML>
<PARAM name="name" datatype="char" arraysize="*" value="photdm-alt">
<VODML><ROLE>vodml-map:Model.name</ROLE><TYPE>ivoa:string</TYPE></VODML>
</PARAM>


</GROUP>


  <RESOURCE ID="photometry_filters">

    <!-- spectralLocation serialized as PARAM -->
    <GROUP  ID="_2massJ">
      <VODML><TYPE>photdm-alt:PhotometryFilter</TYPE></VODML>
      <PARAM name="name" datatype="char" value="J">
        <VODML><ROLE>photdm-alt:PhotometryFilter.name</ROLE></VODML>
      </PARAM>
      <PARAM name="name" datatype="char" value="2mass:J">
        <VODML><ROLE>photdm-alt:PhotometryFilter.bandName</ROLE></VODML>
      </PARAM>
      <PARAM name="name" datatype="float" value="1235" unit="nm">
        <VODML><ROLE>photdm-alt:PhotometryFilter.spectralLocation</ROLE></VODML>
      </PARAM>
    </GROUP>

<!-- Not covering this case because it doesn't make sense for this application:
       - You can serialize the same instance as above.
       - The instance is direct, so you don't need multiple PARAMs as you would need multiple FIELDrefs.
    <!- spectralLocation serialized as GROUP ->
    <GROUP ID="_2massH">
      <VODML><TYPE>photdm-alt:PhotometryFilter</TYPE></VODML>
      <PARAM name="name" datatype="char" value="H">
        <VODML><ROLE>photdm-alt:PhotometryFilter.name</ROLE></VODML>
      </PARAM>
      <PARAM name="name" datatype="char" value="2mass:H">
        <VODML><ROLE>photdm-alt:PhotometryFilter.bandName</ROLE></VODML>
      </PARAM>
      <GROUP>
        <VODML><ROLE>photdm-alt:PhotometryFilter.spectralLocation</ROLE><TYPE>ivoa:quantity.RealQuantity</TYPE></VODML>
        <PARAM name="unit" datatype="char" arraysize="*" value="nm">
          <VODML><ROLE>ivoa:quantity.Quantity.unit</ROLE><TYPE>ivoa:quantity.Unit</TYPE></VODML>
        </PARAM>
        <PARAM name="value" datatype="float"  value="1662">
          <VODML><ROLE>ivoa:quantity.RealQuantity.value</ROLE><TYPE>ivoa:real</TYPE></VODML>
        </PARAM>
      </GROUP>
    </GROUP>
-->

  </RESOURCE>
</VOTABLE>
"""

    private getStream(location) {
        return (new URL(location)).openStream()
    }

    private getExampleString(location) {
        return location
    }
}