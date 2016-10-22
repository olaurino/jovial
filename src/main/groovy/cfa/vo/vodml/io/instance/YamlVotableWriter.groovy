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
