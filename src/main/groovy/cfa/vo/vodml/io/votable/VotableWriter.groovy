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
package cfa.vo.vodml.io.votable

import cfa.vo.vodml.instance.*
import cfa.vo.vodml.utils.VodmlRef
import groovy.xml.StreamingMarkupBuilder

class VotableWriter extends AbstractMarkupInstanceWriter {
    @Override
    void build(DataModelInstance instance, Object builder) {
        def elem = {
            VOTABLE() {
                VODML() {
                    instance.models.each {
                        out << buildModel(it, delegate)
                    }
                    if (instance.objectTypes) {
                        GLOBALS() {
                            instance.objectTypes.each {
                                out << buildObject(it, delegate)
                            }
                        }
                    }

                    instance.globals.each { gl ->
                        GLOBALS(ID: gl.id) {
                            gl.objectTypes.each { inst ->
                                out << buildObject(inst, delegate)
                            }
                        }
                    }

                    instance.resources.each { res ->
                        res.tables.each { tab ->
                            TEMPLATES(tableref: tab.id) {
                                out << buildTable(tab, delegate)
                            }
                        }
                    }

                    instance.tables.each { tab ->
                        TEMPLATES(tableref: tab.id) {
                            out << buildTable(tab, delegate)
                        }
                    }
                }
                instance.resources.each { res ->
                    RESOURCE(ID: res.id) {
                        res.tables.each { tab ->
                            out << buildTableData(tab, delegate)
                        }
                    }
                }
                if (instance.tables) {
                    RESOURCE() {
                        instance.tables.each { tab ->
                            out << buildTableData(tab, delegate)
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
            MODEL() {
                NAME(modelImportInstance.name)
                URL(modelImportInstance.vodmlURL)
                if (modelImportInstance.identifier) {
                    IDENTIFIER(modelImportInstance.identifier)
                }
            }
        }
        elem.delegate = builder
        elem()
    }

    void buildKey(key, tagName, builder) {
        def elem = {
            "$tagName"() {
                PKFIELD() {
                    if (key.value) {
                        LITERAL(dmtype: "ivoa:string", value: key.value)
                    }
                    else {
                        key.columns.each { column ->
                            COLUMN(dmtype: "ivoa:string", ref: column.id)
                        }
                    }
                }
                if (key.hasProperty("target") && key.target) {
                    TARGETID(key.target)
                }
            }
            key.columns.each { column ->
                if (column.role) {
                    ATTRIBUTE(dmrole: roleFilter(column.role)) {
                        def columnAttrs = [dmtype: column.type, ref: column.id]
                        COLUMN(columnAttrs)
                    }
                }
            }
        }
        elem.delegate = builder
        elem()
    }

    void buildObject(objectInstance, builder) {
        def elem = {
            def attrs = [dmtype: objectInstance.type.toString()]
            if (objectInstance.id) {
                attrs.ID = objectInstance.id
            }
            if (objectInstance.value) {
                def literalAttrs = [value: objectInstance.value, dmtype: objectInstance.type.toString()]
                if (objectInstance.unit) {
                    literalAttrs.unit = objectInstance.unit
                }
                LITERAL(literalAttrs)
            } else {
                INSTANCE(attrs) {
                    if (objectInstance.primaryKey) {
                        buildKey(objectInstance.primaryKey, "PRIMARYKEY", builder)
                    }
                    if (objectInstance.foreignKey) {
                        def fk = objectInstance.foreignKey
                        CONTAINER() {
                            buildKey(fk, "FOREIGNKEY", builder)
                        }
                    }
                    objectInstance.objectIndex.each { role, objectInstances ->
                        ATTRIBUTE(dmrole: roleFilter(role)) {
                            objectInstances.each { inst ->
                                out << buildObject(inst, builder)
                            }
                        }
                    }
                    objectInstance.columns.each { column ->
                        ATTRIBUTE(dmrole: roleFilter(column.role)) {
                            def columnAttrs = [dmtype: column.type, ref: column.id]
                            COLUMN(columnAttrs)
                        }
                    }
                    objectInstance.references.each { ref ->
                        REFERENCE(dmrole: roleFilter(ref.role)) {
                            if (ref.idref) {
                                IDREF(ref.idref)
                            } else if (ref.foreignKey) {
                                def fk = ref.foreignKey
                                buildKey(fk, "FOREIGNKEY", builder)
                            } else {
                                REMOTEREFERENCE(ref.remote)
                            }
                        }
                    }
                    if (objectInstance.hasProperty("compositions")) {
                        objectInstance.compositions.each { comp ->
                            out << buildComposition(comp, builder)
                        }
                    }
                }
            }
        }
        elem.delegate = builder
        elem()
    }

    void buildComposition(CompositionInstance compositionInstance, builder) {
        def elem = {
            COMPOSITION(dmrole: roleFilter(compositionInstance.role)) {
                for (instance in compositionInstance.objectTypes) {
                    out << buildObject(instance, builder)
                }
                compositionInstance.externals.each { ext ->
                    EXTINSTANCES(ext.ref)
                }
            }
        }
        elem.delegate = builder
        elem()
    }

    void buildTable(TableInstance tableInstance, builder) {
        def elem = {
            for (instance in tableInstance.objectTypes) {
              out << buildObject(instance, builder)
            }
        }
        elem.delegate = builder
        elem()
    }

    void buildTableData(TableInstance tab, builder) {
        def elem = {
            TABLE(ID: tab.id) {
                tab.columns.each { col ->
                    def attrs = col.infer(col.data[0])
                    attrs['ID'] = col.id
                    attrs['name'] = col.name ?: col.id
                    if (col.unit) {
                        attrs['unit'] = col.unit
                    }
                    FIELD(attrs)
                }
                DATA() {
                    TABLEDATA() {
                        if (tab.columns) {
                            while (tab.columns[0].data.size > 0) {
                                TR() {
                                    tab.columns.each { col ->
                                        def val
                                        try {
                                            val = col.data.pop().toString()
                                        } catch (Exception ignore) {
                                            val = "null"
                                        }
                                        TD(val)
                                    }
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

    private static String roleFilter(VodmlRef roleRef) {
        def role = roleRef.toString()
        def index = role.indexOf(".subsettedBy")
        if (index > 0) {
            return role.substring(0, index)
        } else {
            return role
        }
    }

    @Override
    String getNameSpace() {
        return "http://www.ivoa.net/xml/VOTable/v1.4"
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
