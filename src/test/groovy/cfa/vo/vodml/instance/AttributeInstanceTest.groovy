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

import cfa.vo.vodml.io.DataModelInstanceBuilder
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
        DataModelInstance instance = new DataModelInstanceBuilder().dmInstance {
            model(spec: stcSpec, vodmlURL: "https://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/STC2/prototype/STCPrototype-2.0.vo-dml.xml")
            model(spec: ivoaSpec, vodmlURL: "https://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/ivoa/IVOA.vo-dml.xml")
            instance(type: "ds:experiment.AstroTarget") {
                instance(role: "name", value: "3C273")
                instance(role: "description", value: "A Quasar")
                instance(role: "position", type: "stc:coords.Position") {
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

        expect:
        !instance.objectTypes[0].objectTypes.empty
        !instance.objectTypes[0].objectTypes[2].objectTypes.empty
    }

    def "test inequality"() {
        given:
        def i1 = new ObjectInstance(role: "bar:baz")
        def i2 = new ObjectInstance(type: "foo:bar")

        expect:
        i1 != i2
    }
}
