package cfa.vo.vodml.instance

import cfa.vo.vodml.Model
import cfa.vo.vodml.io.VodmlWriter
import cfa.vo.vodml.utils.Resolver
import cfa.vo.vodml.utils.VoBuilderNode
import cfa.vo.vodml.utils.VodmlRef
import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode


trait VodmlBuildable implements Buildable {
    Set<String> toBuild = []

    void add(String name) {
        toBuild << name
    }

    Closure build = { delegate ->
        toBuild.each { arrayToBuild ->
            this."$arrayToBuild".each {
                delegate.out << it
            }
        }
    }

    @Override
    void build(GroovyObject builder) {
        def callback = getBuildCallback()
        callback.delegate = builder
        callback.call()
    }

    abstract Closure getBuildCallback()
}

trait HasObjects implements VodmlBuildable {
    List<ObjectInstance> objectTypes = []
    public leftShift(ObjectInstance data) {
        objectTypes << data
        add("objectTypes")
    }
}

trait HasData implements VodmlBuildable {
    List<DataInstance> dataTypes = []
    public leftShift(DataInstance data) {
        dataTypes << data
        add("dataTypes")
    }
}

trait HasValues implements VodmlBuildable {
    List<ValueInstance> attributes = []
    public leftShift(ValueInstance type) {
        attributes << type
        toBuild.add("attributes")
    }
}

trait HasReferences implements VodmlBuildable {
    List<ReferenceInstance> references = []
    public leftShift(ReferenceInstance type) {
        references << type
        add("references")
    }
}

trait DefaultNode implements VoBuilderNode {
    Resolver resolver = Resolver.instance
    void start(Map m = [:]) {}
    void apply() {}
    void end() {}
}

@Canonical
@EqualsAndHashCode(excludes="resolver")
class VotableInstance implements DefaultNode, HasObjects, HasData {
    String ns = "http://www.ivoa.net/xml/VOTable/v1.2"
    String prefix = ""
    List<ModelInstance> models = []

    public leftShift(ModelInstance data) {models << data}
    public leftShift(Model data) {resolver << data}

    @Override
    Closure getBuildCallback() {
        def callback = {
            VOTABLE() {
                RESOURCE() {
                    models.each {
                        out << it
                    }
                    build.call(delegate)
                }
            }
        }
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
abstract class Instance implements DefaultNode {
    String id
    def parent
    VodmlRef type
    VodmlRef role
    protected Map attrs

    void setType(String ref) {
        type = new VodmlRef(ref)
    }

    public setRole(String ref) {
        try {
            this.role = this.resolver.resolveAttribute(this.parent.type, ref)
        } catch (Exception ex) {
            this.role = new VodmlRef(ref)
        }
    }

    public void start(Map attrs) {
        this.attrs = attrs
    }

    public void apply() {
        attrs.each {k, v -> this."$k" = v}
    }

    public void end() {

    }
}

@Canonical
class DataInstance extends Instance implements HasData, HasReferences, HasValues {

    @Override
    Closure getBuildCallback() {
        def callback = {
            def m = [:]
            if (this.id) {
                m.ID = this.id
            }

            def vodmlm = [:]

            if (role) {
                vodmlm.role = role
            }

            if (type) {
                vodmlm.type = type
            }
            def elem = {
                GROUP(m) {
                    out << new Vodml(vodmlm)
                    build.call(delegate)
                }
            }
        }
    }
}

@Canonical
class ObjectInstance extends Instance implements HasValues, HasData, HasReferences {
    private List<CollectionInstance> collections = []

    public leftShift(CollectionInstance object) {collections << object}

    @Override
    Closure getBuildCallback() {
        def callback = {
            def m = [:]
            if (id) {
                m.ID = id
            }

            def vodmlm = [:]

            if (role) {
                vodmlm.role = role
            }

            if (type) {
                vodmlm.type = type
            }
            GROUP(m) {
                out << new Vodml(vodmlm)
                build.call(delegate)
                collections.each {
                    out << it
                }
            }
        }
    }
}

@Canonical
class CollectionInstance extends Instance implements HasObjects {

    public leftShift(ObjectInstance object) {
        HasObjects.super.leftShift(object)
        object.role = role
    }

    @Override
    Closure getBuildCallback() {
        def callback = {
            build.call(delegate)
        }
    }
}

@Canonical
class ReferenceInstance extends Instance {
    String value

    @Override
    void build(GroovyObject builder) {
        def elem = {
            GROUPref(ref: value) {
                out << new Vodml(role: role)
            }
        }
        elem.delegate = builder
        if (value != null) {
            elem()
        }
    }
}

@Canonical(includes=["type","role"])
class ValueInstance extends Instance {
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

    private String stripRole() {
        role.reference.split("\\.")[-1] ?: "none"
    }

    private Map paramAttrs() {
        String name = resolver.resolveRole(this.role)?.name ?: stripRole()
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
    void end() {
        super.end()
        parent << spec
    }

    @Override
    void build(GroovyObject builder) {
        def object = new ObjectInstance(type: "vo-dml:Model")
        object << new ValueInstance(role: new VodmlRef("vo-dml:Model.name"), value:spec.name)
        object << new ValueInstance(role: new VodmlRef("vo-dml:Model.version"), value:spec.version)
        object << new ValueInstance(role: new VodmlRef("vo-dml:Model.url"), value:vodmlURL)
        object << new ValueInstance(role: new VodmlRef("vo-dml:Model.documentationURL"), value:documentationURL)
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

