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
package cfa.vo.vodml.io.instance

import cfa.vo.vodml.instance.*
import cfa.vo.vodml.io.VoTableBuilder
import cfa.vo.vodml.utils.Resolver
import cfa.vo.vodml.utils.VodmlRef

class VotableWriter extends AbstractMarkupInstanceWriter {
    public static final VODML_PREF = "vodml-map"
    String nameSpace = "http://www.ivoa.net/xml/VOTable/v1.3_vodml"
    String prefix = ""

    void build(DataModelInstance instance, builder) {
        def elem = {
            VOTABLE() {
                instance.models.each {
                    out << buildModel(it, delegate)
                }
                RESOURCE() {
                    instance.objectTypes.each {
                        out << buildObject(it, delegate)
                    }
                }
            }
        }
        elem.delegate = builder
        elem()
    }

    void buildObject(ObjectInstance objectInstance, builder) {
        def elem = {
            def m = [:]
            if (objectInstance.id) {
                m.ID = objectInstance.id
            }

            def vodmlm = [:]

            if (objectInstance.role) {
                vodmlm.role = objectInstance.role
            }

            if (objectInstance.type) {
                vodmlm.type = objectInstance.type
            }
            GROUP(m) {
                out << new Vodml(vodmlm)
                objectInstance.attributes.each {
                    out << buildValue(it, builder)
                }
                objectInstance.dataTypes.each {
                    out << buildData(it, builder)
                }
                objectInstance.references.each {
                    out << buildReference(it, builder)
                }
                objectInstance.collections.each {
                    out << buildCollection(it, builder)
                }
            }
            mkp.comment(
                    "End ObjectType role: ${objectInstance.role ?: "{No Role}"} type: ${objectInstance.type ?: "{No Type}"}")
        }
        elem.delegate = builder
        elem()
    }

    void buildModel(ModelImportInstance modelInstance, builder) {
        def object = new VoTableBuilder().object(type: "$VODML_PREF:Model") {
            value(role: "$VODML_PREF:Model.url", type: "ivoa:anyURI", value: modelInstance.vodmlURL)
            if(modelInstance.identifier) {
                value(role: "$VODML_PREF:Model.identifier", type:"ivoa:anyURI", value:modelInstance.identifier)
            }
            value(role: "$VODML_PREF:Model.name", type:"ivoa:string", value:modelInstance.spec.name)
            if(modelInstance.documentationURL) {
                value(role: "$VODML_PREF:Model.documentationURL", type: "ivoa:anyURI",
                        value: modelInstance.documentationURL)
            }
        }
        def elem = {
            out << buildObject(object, builder)
        }
        elem.delegate = builder
        elem()
    }

    void buildData(DataInstance dataInstance, builder) {
        def elem = {
            def m = [:]
            if (dataInstance.id) {
                m.ID = dataInstance.id
            }

            def vodmlm = [:]

            if (dataInstance.role) {
                vodmlm.role = dataInstance.role
            }

            if (dataInstance.type) {
                vodmlm.type = dataInstance.type
            }
            GROUP(m) {
                out << new Vodml(vodmlm)
                dataInstance.attributes.each {
                    out << buildValue(it, builder)
                }
                dataInstance.dataTypes.each {
                    out << buildData(it, builder)
                }
                dataInstance.references.each {
                    out << buildReference(it, builder)
                }
            }
            mkp.comment(
                    "End DataType role: ${dataInstance.role ?: "{No Role}"} type: ${dataInstance.type ?: "{No Type}"}")
        }
        elem.delegate = builder
        elem()
    }

    void buildCollection(CollectionInstance collectionInstance, builder) {
        def elem = {
            collectionInstance.objectTypes.each {
                out << buildObject(it, delegate)
                mkp.comment("End Collection role: ${collectionInstance.role ?: "{No Role}"}")
            }
        }
        elem.delegate = builder
        elem()
    }

    void buildReference(ReferenceInstance referenceInstance, builder) {
        def elem = {
            if (referenceInstance.value != null) {
                GROUP(ref: referenceInstance.value) {
                    out << new Vodml(role: referenceInstance.role)
                }
            }
        }
        elem.delegate = builder
        elem()
    }

    void buildValue(ValueInstance valueInstance, builder) {
        def elem = {
            if (valueInstance.value != null) {
                PARAM(paramAttrs(valueInstance)) {
                    out << new Vodml(role: valueInstance.role, type: valueInstance.type)
                }
            }
        }
        elem.delegate = builder
        elem()
    }

    private static String stripRole(ValueInstance instance) {
        instance.role.reference.split("\\.")[-1] ?: "none"
    }

    private static Map paramAttrs(ValueInstance instance) {
        String name = Resolver.instance.resolveRole(instance.role)?.name ?: stripRole(instance)
        Map datatype = infer(instance.value)
        datatype << [name:name, value:instance.value]
    }

    static Map infer(value) {
        def dt = "char"
        def ars = "*"
        if(value in String) { // Simple case, it's a string
            dt = "char"
            ars = value.length().toString()
        } else if (value in Number) { // or it's a number
            dt = [Integer.class, int.class].any {it.isAssignableFrom(value.class)} ? "int" : "float"
            ars = "1"
        } else if ([Collection, Object[]].any {it.isAssignableFrom(value.class)}) { // it's an array
            dt = infer(value[0]).datatype
            if (value.hasProperty("length")) {
                ars = value.length.toString()
            } else if (value.respondsTo("size")) {
                ars = value.size().toString()
            }
        }

        [datatype: dt, arraysize: ars]
    }

    /**
     * Class implementing the VODML element and its serialization.
     */
    class Vodml implements Buildable {
        VodmlRef type
        VodmlRef role

        @Override
        void build(GroovyObject builder) {
            def elem = {
                VODML() {
                    if (role) {
                        ROLE() {
                            out << role
                        }
                    }
                    if (type) {
                        TYPE() {
                            out << type
                        }
                    }
                }
            }
            elem.delegate = builder
            elem()
        }
    }
}
