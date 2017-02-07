/*
 * #%L
 * jovial
 * %%
 * Copyright (C) 2016 - 2017 Smithsonian Astrophysical Observatory
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
package cfa.vo.vodml.io.instance

import cfa.vo.vodml.instance.*
import groovy.xml.StreamingMarkupBuilder

class AltVotableWriter extends AbstractMarkupInstanceWriter {
    @Override
    void build(DataModelInstance instance, Object builder) {
        def elem = {
            VOTABLE() {
                VODML() {
                    instance.models.each {
                        out << buildModel(it, delegate)
                    }
                    GLOBALS() {
                        instance.objectTypes.each {
                            out << buildObject(it, delegate, false)
                        }
                    }
                    TEMPLATES() {
                        instance.tables.each {
                            out << buildTable(it, delegate)
                        }
                    }
                }
            }
        }
        elem.delegate = builder
        elem()
    }

    void buildModel(ModelImportInstance modelImportInstance, builder) {
        def elem = {
            def attrs = [
                    name: modelImportInstance.name,
                    url : modelImportInstance.vodmlURL,
            ]
            if (modelImportInstance.identifier) {
                attrs['identifier'] = modelImportInstance.identifier
            }
            MODEL(attrs)
        }
        elem.delegate = builder
        elem()
    }

    void buildObject(objectInstance, builder, slot="slot") {
        def elem = { tagname = "INSTANCE" ->
            "$tagname"(dmtype:objectInstance.type.toString(), id: objectInstance.id) {
                objectInstance.attributes.each {
                    out << buildValue(it, builder)
                }
//                objectInstance.dataTypes.each {
//                    out << buildObject(it, builder, "slot")
//                }
                objectInstance.references.each {
                    REFERENCE(dmrole: it.role, it.value)
                }
                if (objectInstance.hasProperty("collections")) {
                    objectInstance.collections.each {
                        out << buildCollection(it, builder)
                    }
                }
                objectInstance.columns.each { column ->
                    ATTRIBUTE(dmrole: column.role) {
                        COLUMN(ref: column.value)
                    }
                }
            }
        }
        def wrapper = {
            if ("slot".equals(slot)) {
                ATTRIBUTE(dmrole:objectInstance.role.toString()) {
                    elem("INSTANCE")
                }
            } else if ("template".equals(slot)) {
                elem("TEMPLATE")
            } else {
                elem()
            }
        }
        wrapper.delegate = builder
        elem.delegate = builder
        wrapper()
    }

    void buildCollection(CompositionInstance collectionInstance, builder) {
        def elem = {
            COMPOSITION(dmrole:collectionInstance.role.toString()) {
                for (instance in collectionInstance.objectTypes) {
                    out << buildObject(instance, builder, false)
                }
            }
        }
        elem.delegate = builder
        elem()
    }

    void buildTable(TableInstance tableInstance, builder) {
        def elem = {
            for (instance in tableInstance.objectTypes) {
              out << buildObject(instance, builder, "")
            }
        }
        elem.delegate = builder
        elem()
    }

    void buildValue(LiteralInstance valueInstance, builder) {
        def elem = {
            ATTRIBUTE(dmrole: valueInstance.role) {
                LITERAL(valueInstance.value, dmtype: valueInstance.type.toString())
            }
        }
        elem.delegate = builder
        elem()
    }

    @Override
    String getNameSpace() {
        return "http://www.ivoa.net/xml/VOTable/v1.3_vodmlAlt"
    }

    @Override
    String getPrefix() {
        return ""
    }

    @Override
    StreamingMarkupBuilder getMarkupBuilder() {
        return new StreamingMarkupBuilder()
    }
}
