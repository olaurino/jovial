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
