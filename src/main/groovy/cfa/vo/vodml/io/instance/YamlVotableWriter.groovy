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

import cfa.vo.vodml.instance.DataModelInstance
import cfa.vo.vodml.utils.Resolver
import org.yaml.snakeyaml.Yaml

class YamlVotableWriter {
    private DataModelInstance instance

    void write(DataModelInstance instance, OutputStream os) {
        this.instance = instance
        def builder = new Yaml()
        def object = build(instance)
        String out = builder.dump(object)
        os.write(out.bytes)
        os.close()
    }

    def build(DataModelInstance instance) {
        def registry = [:]
        def object = [:]
        object.votable = [:]
        object.votable.models = []
        instance.models.each { modelImportInstance ->
            def model = [:]
            model.name = modelImportInstance.name
            model.url = modelImportInstance.vodmlURL
            if (modelImportInstance.identifier) {
                model.identifier = modelImportInstance.identifier
            }
            object.votable.models += model
        }
        object.votable.standalone = []
        instance.objectTypes.each { objectType ->
            object.votable.standalone += buildObject(objectType, registry)
        }
        object.votable.tables = []
        instance.tables.each { table ->
            table.objectTypes.each { objectType ->
                object.votable.tables += buildObject(objectType, registry)
            }
        }

        return object
    }

    def buildObject(objectInstance, registry) {

        def object = [:]
        object.type = objectInstance.type.toString()
        if (objectInstance.id) {
            object.id = objectInstance.id
            registry[object.id] = object
        }
        objectInstance.attributes.each { valueInstance ->
            def name = Resolver.instance.resolveRole(valueInstance.role).name
            object[name] = valueInstance.value
        }
        objectInstance.dataTypes.each { dataInstance ->
            def name = Resolver.instance.resolveRole(dataInstance.role).name
            object[name] = buildObject(dataInstance, registry)
        }
        objectInstance.references.each { reference ->
            def name = Resolver.instance.resolveRole(reference.role).name
            object[name] = registry[reference.value]
        }
        objectInstance.collections.each { collection ->
            def name = Resolver.instance.resolveRole(collection.role).name
            object[name] = []
            collection.objectTypes.each { objectType ->
                object[name] += buildObject(objectType, registry)
            }
        }
        if (objectInstance.columns) {
            object.columns = [:]
            objectInstance.columns.each { col ->
                def name = Resolver.instance.resolveRole(col.role).name
                def column = [ref: col.value, pk: col.pk, fk: col.fk]
                object.columns[name] = column
            }
        }
        return object
    }
}
