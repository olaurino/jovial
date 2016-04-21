package cfa.vo.vodml.instance

import cfa.vo.vodml.Model
import cfa.vo.vodml.utils.Resolver
import cfa.vo.vodml.utils.VoBuilderNode
import cfa.vo.vodml.utils.VodmlRef
import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode


@Canonical
@EqualsAndHashCode(excludes="resolver")
class Votable {
    List<Model> models = []
    List<DataInstance> dataTypes = []
    List<ObjectInstance> objectTypes = []
    def resolver = new Resolver()

    public setModel(Model spec) {
        models << spec
        resolver << spec
    }

    public leftShift(DataInstance data) {dataTypes << data}
    public leftShift(ObjectInstance data) {objectTypes << data}

    def init(Map m = [:]) {}
    def finish() {}
}


@Canonical(excludes=["resolver", "attrs", "parent"])
abstract class Instance implements VoBuilderNode {
    protected Map attrs
    def parent
    Resolver resolver

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
}

@Canonical
class ObjectInstance extends Instance {
    VodmlRef type
    List<ValueInstance> attributes = []

    public ObjectInstance(String vodmlref) {
        type = new VodmlRef(vodmlref)
    }

    public leftShift(ValueInstance type) {attributes << type}
}

class ValueInstance extends Instance {
    VodmlRef role
    def value

    public setRole(String ref) {
        try {
            this.role = resolver.resolveAttribute(parent.type, ref)
        } catch (Exception ex) {
            this.role = new VodmlRef(ref)
        }
    }
}

