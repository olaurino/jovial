package cfa.vo.vodml.io.instance

import cfa.vo.vodml.instance.*
import groovy.xml.MarkupBuilder
import groovy.xml.StreamingMarkupBuilder

class AltVotableWriter extends AbstractMarkupInstanceWriter {
    @Override
    void build(DataModelInstance instance, Object builder) {
        def elem = {
            VOTABLE() {
                RESOURCE(id: "globals") {
                    instance.models.each {
                        out << buildModel(it, delegate)
                    }
                }
                RESOURCE(id: "standalone") {
                    instance.objectTypes.each {
                        out << buildObject(it, delegate, false)
                    }
                }
                RESOURCE(id: "tables") {
                    instance.tables.each {
                        out << buildTable(it, delegate)
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
            "$tagname"(type:objectInstance.type.toString(), id: objectInstance.id) {
                objectInstance.attributes.each {
                    out << buildValue(it, builder)
                }
                objectInstance.dataTypes.each {
                    out << buildObject(it, builder, "slot")
                }
                objectInstance.references.each {
                    REFERENCE(role: it.role, it.value)
                }
                if (objectInstance.hasProperty("collections")) {
                    objectInstance.collections.each {
                        out << buildCollection(it, builder)
                    }
                }
                objectInstance.columns.each { column ->
                    SLOT(role: column.role) {
                        COLUMN(pk: column.pk, fk: column.fk, column.value)
                    }
                }
            }
        }
        def wrapper = {
            if ("slot".equals(slot)) {
                SLOT(role:objectInstance.role.toString()) {
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

    void buildCollection(CollectionInstance collectionInstance, builder) {
        def elem = {
            SLOT(role:collectionInstance.role.toString()) {
                COLLECTION() {
                    for (instance in collectionInstance.objectTypes) {
                        out << buildObject(instance, builder, false)
                    }
                }
            }
        }
        elem.delegate = builder
        elem()
    }

    void buildTable(TableInstance tableInstance, builder) {
        def elem = {
            for (instance in tableInstance.objectTypes) {
              out << buildObject(instance, builder, "template")
            }
        }
        elem.delegate = builder
        elem()
    }

    void buildValue(ValueInstance valueInstance, builder) {
        def elem = {
            VALUE(valueInstance.value, type: valueInstance.type.toString(), role: valueInstance.role)
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
    MarkupBuilder getMarkupBuilder() {
        return new StreamingMarkupBuilder()
    }
}
