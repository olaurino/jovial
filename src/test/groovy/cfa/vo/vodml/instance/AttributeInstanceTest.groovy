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
package cfa.vo.vodml.instance

import cfa.vo.vodml.io.VoTableBuilder
import cfa.vo.vodml.io.VodmlReader
import cfa.vo.vodml.metamodel.Model
import spock.lang.Specification

class AttributeInstanceTest extends Specification {
    Model stcSpec
    Model ivoaSpec

    def setup() {
        def reader = new VodmlReader()
        ivoaSpec = reader.read(getClass().getResource("/ivoa.vo-dml.xml").openStream())
        stcSpec = reader.read(getClass().getResource("/stc2.vo-dml.xml").openStream())
    }

    def "test position"() {
        given:
        DataModelInstance instance = new VoTableBuilder().votable {
            model(spec: stcSpec, vodmlURL: "https://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/STC2/prototype/STCPrototype-2.0.vo-dml.xml")
            model(spec: ivoaSpec, vodmlURL: "https://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/ivoa/IVOA.vo-dml.xml")
            object(type: "ds:experiment.AstroTarget") {
                value(role: "name", value: "3C273")
                value(role: "description", value: "A Quasar")
                data(role: "position", type: "stc:coords.Position") {
                    data(role: "coord", type: "stc:stctypes.RealDoublet") {
                        value(role: "d1", value: 187.2792)
                        value(role: "d2", value: 2.0525)
                    }
                }
                value(role: "objectClass", value: "BLAZAR")
                value(role: "spectralClass", value: "Sy1")
                value(role: "redshift", value: 0.158)
                value(role: "varAmpl", value: Double.NaN)
            }
        }

        expect:
        !instance.objectTypes[0].dataTypes.empty
        !instance.objectTypes[0].dataTypes[0].dataTypes.empty
    }

    def "test inequality"() {
        given:
        def i1 = new AttributeInstance(attrs: [:])
        def i2 = new AttributeInstance(attrs: [type: "foo:bar"])

        expect:
        i1 != i2
    }
}
