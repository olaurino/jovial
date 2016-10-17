package cfa.vo.vodml.io.instance

import cfa.vo.vodml.instance.*

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

    void buildObject(objectInstance, builder, slot=true) {
        def elem = {
            INSTANCE(type:objectInstance.type.toString()) {
                objectInstance.attributes.each {
                    out << buildValue(it, builder)
                }
                objectInstance.dataTypes.each {
                    out << buildObject(it, builder, true)
                }
//                objectInstance.references.each {
//                    out << buildReference(it, builder)
//                }
                if (objectInstance.hasProperty("collections")) {
                    objectInstance.collections.each {
                        out << buildCollection(it, builder)
                    }
                }
            }
        }
        def wrapper = {
            if (slot) {
                SLOT(role:objectInstance.role.toString()) {
                    elem()
                }
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
            SLOT(collection:true, role:collectionInstance.role.toString()) {
                for (instance in collectionInstance.objectTypes) {
                    out << buildObject(instance, builder, false)
                }
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

//    void buildData(DataInstance dataInstance, builder) {
//        def elem = {
//            INSTANCE(type:dataInstance.type.toString(), role:dataInstance.role.toString()) {
//                dataInstance.attributes.each {
//                    out << buildValue(it, builder)
//                }
//                dataInstance.dataTypes.each {
//                    out << buildData(it, builder)
//                }
////                objectInstance.references.each {
////                    out << buildReference(it, builder)
////                }
//            }
//        }
//        elem.delegate = builder
//        elem()
//    }

    @Override
    String getNameSpace() {
        return "http://www.ivoa.net/xml/VOTable/v1.3_vodmlAlt"
    }

    @Override
    String getPrefix() {
        return ""
    }
}
