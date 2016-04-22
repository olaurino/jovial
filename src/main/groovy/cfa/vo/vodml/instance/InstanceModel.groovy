package cfa.vo.vodml.instance

import cfa.vo.vodml.Model
import cfa.vo.vodml.io.VodmlWriter
import cfa.vo.vodml.utils.Resolver
import cfa.vo.vodml.utils.VoBuilderNode
import cfa.vo.vodml.utils.VodmlRef
import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode


@Canonical
@EqualsAndHashCode(excludes="resolver")
class VotableInstance implements VoBuilderNode {
    String ns = "http://www.ivoa.net/xml/VOTable/v1.2"
    String prefix = ""
    List<ModelInstance> models = []
    List<DataInstance> dataTypes = []
    List<ObjectInstance> objectTypes = []
    Resolver resolver = Resolver.instance

    public leftShift(DataInstance data) {dataTypes << data}
    public leftShift(ObjectInstance data) {objectTypes << data}
    public leftShift(ModelInstance data) {models << data}
    public leftShift(Model data) {resolver << data}

    void init(Map m = [:]) {}
    void finish() {}

    @Override
    void build(GroovyObject builder) {
        def elem = {
            VOTABLE() {
                RESOURCE() {
                    models.each {
                        out << it
                    }
                }
            }
        }
        elem.delegate = builder
        elem()
    }


    /**
     * Serialize this instance to an OutputStream
     * @param os
     */
    public toXml(OutputStream os) {
        VodmlWriter write = new VodmlWriter()
        write.write(this, os)
    }
}


@Canonical(excludes=["resolver", "attrs", "parent"])
abstract class Instance implements VoBuilderNode {
    protected Map attrs
    def parent
    Resolver resolver = Resolver.instance

    public void init(Map attrs) {
        this.attrs = attrs
    }

    public void finish() {
        attrs.each {k, v -> this."$k" = v}
    }
}

@Canonical
class DataInstance extends Instance {
    VodmlRef type
    List<ValueInstance> attributes = []

    public DataInstance(String vodmlref) {
        type = new VodmlRef(vodmlref)
    }

    public leftShift(ValueInstance type) {attributes << type}

    @Override
    void build(GroovyObject groovyObject) {

    }
}

@Canonical
class ObjectInstance extends Instance {
    VodmlRef type
    VodmlRef role
    List<ValueInstance> attributes = []
    List<CollectionInstance> collections = []
//    List<DataTypeInstance> dataAttributes = []
//    List<ReferenceInstance> references = []

    public ObjectInstance(String vodmlref) {
        type = new VodmlRef(vodmlref)
    }

//    public setType(String ref) {
//        type = new VodmlRef(ref)
//    }

    public leftShift(ValueInstance type) {attributes << type}

    @Override
    void build(GroovyObject builder) {
        def elem = {
            GROUP() {
                out << new Vodml(type: type)
                attributes.each {
                    out << it
                }
            }
        }
        elem.delegate = builder
        elem()
    }
}

@Canonical
class CollectionInstance extends Instance {
    VodmlRef role
    List<ObjectInstance> objectInstances = []
    List<DataInstance> dataInstances = []

    @Override
    void build(GroovyObject builder) {

    }
}

@Canonical
class ValueInstance extends Instance {
    VodmlRef role
    def value

    @Override
    void build(GroovyObject builder) {
        def elem = {
            PARAM(paramAttrs()) {
                out << new Vodml(role: role)
            }
        }
        elem.delegate = builder
        if (value != null) {
            elem()
        }
    }

    public setRole(String ref) {
        try {
            this.role = this.resolver.resolveAttribute(this.parent.type, ref)
        } catch (Exception ex) {
            this.role = new VodmlRef(ref)
        }
    }

    private String stripRole() {
        role.reference.split("\\.")[-1] ?: "none"
    }

    private Map paramAttrs() {
        String name = resolver.resolveRole(role)?.name ?: stripRole()
        Map datatype = infer(value)
        datatype << [name:name, value:value]
    }

    static Map infer(value) {
        def dt = "char"
        def ars = "*"
        if (value == null) {

        }
        else if(value in String) { // Simple case, it's a string
            dt = "char"
            ars = value.length().toString()
        } else if (value in Number) { // or it's a number
            dt = [Integer.class, int.class].any {it.isAssignableFrom(value.class)} ? "int" : "float"
            ars = "1"
        } else if ([Collection, Object[]].any {it.isAssignableFrom(value.class)}) { // it's an array
            dt = infer(value[0]).datatype
            if (value.hasProperty("length")) {
                ars = value.length.toString()
            } else if (value.metaClass.respondsTo(value, "size")) {
                ars = value.size().toString()
            }
        }

        [datatype: dt, arraysize: ars]
    }
}

class ModelInstance extends Instance {
    String vodmlURL
    String documentationURL
    Model spec

    @Override
    void finish() {
        super.finish()
        parent << spec
    }

    @Override
    void build(GroovyObject builder) {
        def object = new ObjectInstance("vo-dml:Model")
        object.attributes << new ValueInstance(role: "vo-dml:Model.name", value:spec.name)
        object.attributes << new ValueInstance(role: "vo-dml:Model.version", value:spec.version)
        object.attributes << new ValueInstance(role: "vo-dml:Model.url", value:vodmlURL)
        object.attributes << new ValueInstance(role: "vo-dml:Model.documentationURL", value:documentationURL)
        def elem = {
            out << object
        }
        elem.delegate = builder
        elem()
    }
}

class Vodml implements Buildable {
    VodmlRef type
    VodmlRef role

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

