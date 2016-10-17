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

import cfa.vo.vodml.instance.ModelImportInstance
import cfa.vo.vodml.instance.ObjectInstance
import cfa.vo.vodml.instance.DataModelInstance
import cfa.vo.vodml.utils.Resolver
import groovy.xml.MarkupBuilder

class VodmlWriter implements InstanceWriter {

    @Override
    void write(DataModelInstance instance, OutputStream os) {
        def xml = new MarkupBuilder(new OutputStreamWriter(os))
        xml.mkp.xmlDeclaration(version: '1.0', encoding: "utf-8")
        xml."vodmli:instance"('xmlns:vodmli': 'http://volute.g-vo.org/dm/vo-dml-instance/v0.x') {
            for (ModelImportInstance m in instance.models) {
                model() {
                    vodmlURL(m.vodmlURL)
                    vodmlrefPrefix(m.name)
                    if (m.identifier) {
                        identifier(m.identifier)
                    }
                }
            }
            for (ObjectInstance obj in instance.objectTypes) {
                this.objectSer(delegate, obj, "object")
            }
        }
    }

    def objectSer = { del, obj, tag ->
        delegate = del
        "$tag"(vodmlRef: obj.type) {
            for (attr in obj.attributes) {
                def name = Resolver.instance.resolveRole(attr.role).name
                attribute(vodmlRef: attr.role, name: name) {
                    primitiveValue(vodmlRef: attr.type, attr.value)
                }
            }
            for (attr in obj.dataTypes) {
                def name = Resolver.instance.resolveRole(attr.role).name
                attribute(vodmlRef: attr.role, name: name) {
                    objectSer(del, attr, "dataObject")
                }
            }
            if (obj.hasProperty("collections")) {
                for (col in obj.collections) {
                    def name = Resolver.instance.resolveRole(col.role).name
                    collection(vodmlRef: col.role, name: name) {
                        for (instance in col.objectTypes) {
                            objectSer(del, instance, "object")
                        }
                    }
                }
            }
        }
    }
}
