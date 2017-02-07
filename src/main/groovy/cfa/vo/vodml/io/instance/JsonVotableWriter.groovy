package cfa.vo.vodml.io.instance

import cfa.vo.vodml.instance.*
import cfa.vo.vodml.utils.Resolver
import groovy.json.JsonBuilder

class JsonVotableWriter {
    private DataModelInstance instance

    void write(DataModelInstance instance, OutputStream os) {
        this.instance = instance
        def writer = new OutputStreamWriter(os)
        def builder = new JsonBuilder()
        builder.votable {
            build(instance, delegate)
        }
        writer.write(builder.toPrettyString())
        writer.close()
    }

    void build(DataModelInstance instance, Object builder) {
        def elem = {
            models {
                instance.models.each { modelImportInstance ->
                    "$modelImportInstance.name" {
                        url modelImportInstance.vodmlURL
                        if (modelImportInstance.identifier) {
                            identifier modelImportInstance.identifier;

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

                        }
                    }
                }
            }
            standalone {
                instance.objectTypes.each { objectType ->
                    buildObject(objectType, delegate)
                }
            }
            tables {
                instance.tables.collect { table ->
                    table.objectTypes.each { objectType ->
                        buildObject(objectType, delegate)
                    }
                }
            }
        }
        elem.delegate = builder
        elem()
    }

    def buildObject(objectInstance, builder) {
        def elem = {
            "$objectInstance.type"{
                if (objectInstance.id) {
                    id objectInstance.id
                }
                objectInstance.attributes.each { valueInstance ->
                    def name = Resolver.instance.resolveRole(valueInstance.role).name
                    "$name"(valueInstance.value)
                }
                objectInstance.dataTypes.each { dataInstance ->
                    buildObject(dataInstance, builder)
                }
                if (objectInstance.references) {
                    references {
                        objectInstance.references.each { reference ->
                            def name = Resolver.instance.resolveRole(reference.role).name
                            "$name"(reference.value)
                        }
                    }
                }
                if (objectInstance.hasProperty("collections")) {
                    objectInstance.collections.each { collection ->
                        def name = Resolver.instance.resolveRole(collection.role).name
                        "$name" collection.objectTypes.collect { item ->
                            buildObject(item, builder)
                        }
                    }
                }
                if (objectInstance.columns) {
                    columns {
                        objectInstance.columns.each { col ->
                            def name = Resolver.instance.resolveRole(col.role).name
                            "$name" {
                                ref(col.value)
                                if (col.pk) {
                                    pk(col.pk)
                                }
                                if (col.fk) {
                                    fk(col.fk)
                                }
                            }
                        }
                    }
                }
            }
        }
        elem.delegate = builder
        elem()
    }
}
