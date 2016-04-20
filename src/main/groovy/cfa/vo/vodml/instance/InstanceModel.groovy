package cfa.vo.vodml.instance

import groovy.transform.Canonical

@Canonical
class VodmlRef {
    String prefix
    String reference

    @Override
    String toString() {
        return "$prefix:$reference"
    }
}

class Instance implements Buildable {
    String prefix = ""
    String ns = "http://www.ivoa.net/xml/VOTable/v1.2"
    String schemaLocation = "http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/xsd/vo-dml-v1.0.xsd"
    List<ModelLocation> models = []
    List<ObjectInstance> objectInstances = []
    List<DataInstance> dataInstances = []

    @Override
    void build(GroovyObject builder) {
        def elem = {
            VOTABLE("xsi:schemaLocation": "${this.ns} ${this.schemaLocation}") {
                RESOURCE {
                    this.models.each {
                        out << it
                    }
                }
            }
        }
        elem.delegate = builder
        elem()
    }
}

class VodmlInstance extends DataInstance {
    private final PREFIX = "vo-dml"
    private final NS = "Model"
    private final NAME = new VodmlRef(PREFIX, "${NS}.name")
    private final VERSION = new VodmlRef(PREFIX, "${NS}.version")
    private final URL = new VodmlRef(PREFIX, "${NS}.url")
    private final DOC_URL = new VodmlRef(PREFIX, "${NS}.documentationURL")
    VodmlRef type = new VodmlRef(PREFIX, NS)

    VodmlInstance(String name, String version, URL url, URL documentationURL) {
        attributes = [
                new PrimitiveAttribute(role: NAME, value: name),
                new PrimitiveAttribute(role: VERSION, value: version),
                new PrimitiveAttribute(role: URL, value: url),
                new PrimitiveAttribute(role: DOC_URL, value: documentationURL),
        ]
    }
}

@Canonical
class VODML implements Buildable {
    String type
    String role

    @Override
    void build(GroovyObject builder) {
        def elem = {
            VODML() {
                if (type) {
                    TYPE() {
                        out << type
                    }
                }
                if (role) {
                    ROLE() {
                        out << role
                    }
                }
            }
        }
        elem.delegate = builder
        elem()
    }
}

@Canonical
class ModelLocation implements Buildable {
    String prefix
    String version
    URL vodmlUrl
    URL documentationURL

    @Override
    void build(GroovyObject builder) {
        def elem = {
            out << new VodmlInstance(prefix, version, vodmlUrl, documentationURL)
        }
        elem.delegate = builder
        elem()
    }
}

class ObjectInstance {
    ObjectId identifier
    List<Attribute> attributes = []
    List<Reference> references = []
    Reference container
    List<Collection> collections = []
}

class ObjectId {
    String transientId
    CustomId publishereDID
    AltId altId
}

class CustomId {
    List<String> fields
}

class AltId extends CustomId {
    String source
}

abstract class Attribute {
    VodmlRef role
    VodmlRef type
    String name
}

class DataAttribute extends Attribute {
    DataInstance value
}

class DataInstance implements Buildable {
    List<Attribute> attributes = []
    List<Reference> references = []
    VodmlRef type
    VodmlRef role

    @Override
    void build(GroovyObject builder) {
        def elem = {
            GROUP() {
                out << new VODML(type: type, role: role)
                attributes.each {
                    out << it
                }
            }
        }
        elem.delegate = builder
        elem()
    }
}

class PrimitiveAttribute implements Buildable {
    VodmlRef role
    VodmlRef type
    def value

    @Override
    void build(GroovyObject builder) {
        def elem = {
            if (value) {
                PARAM(
                        value: value,
                )
                out << new VODML(type: type, role: role)
            }
        }
        elem.delegate = builder
        elem()
    }
}

class Collection {
    List<ObjectInstance> instances
    VodmlRef ref
    String name
}

class Reference {
    URL objectDocument
    ObjectId identifier
    VodmlRef role
    VodmlRef type
    String name

}
