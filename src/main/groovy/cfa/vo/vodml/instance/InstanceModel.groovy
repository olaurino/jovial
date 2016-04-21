package cfa.vo.vodml.instance

import cfa.vo.vodml.ModelSpec
import cfa.vo.vodml.Role
import cfa.vo.vodml.Type
import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode

@Canonical
class VodmlRef {
    String prefix
    String reference

    public VodmlRef(String ref) {
        List tokens = ref.split(":") as List // Lists return null if index is out of bounds
        prefix = tokens[1] ? tokens[0] : null // No ':' means no prefix
        reference = tokens[1] ?: tokens[0]
    }

    public VodmlRef(String prefix, String reference) {
        this.prefix = prefix
        this.reference = reference
    }

    public VodmlRef(VodmlRef ref) {
        this.prefix = ref.prefix
        this.reference = ref.reference
    }

    @Override
    String toString() {
        return "$prefix:$reference"
    }
}

class VOTableBuilder extends BuilderSupport {

    private static final String MODEL_PACKAGE = "cfa.vo.vodml.instance"

    private static final Map SUPPORTED_MODELS = [build: ArrayList,].withDefault {
        Class.forName("${MODEL_PACKAGE}.${it.capitalize()}")
    }

    @Override
    protected void setParent(Object parent, Object child) {
        if (parent == child) return
        child.parent = parent
        child.resolver = parent.resolver
        parent << child
    }

    @Override
    protected Object createNode(Object name) {
        return createNode(name, null, null)
    }

    @Override
    protected Object createNode(Object name, Object value) {
        return createNode(name, null, value)
    }

    @Override
    protected Object createNode(Object name, Map attrs) {
        return createNode(name, attrs, null)
    }

    @Override
    protected Object createNode(Object name, Map attrs, Object value) {
        try {
            def instance
            if (!value) {
                instance = SUPPORTED_MODELS[name].newInstance()
            }
            else {
                instance = SUPPORTED_MODELS[name].newInstance(value)
            }
            instance.init(attrs)
            return instance
        }
        catch (ClassNotFoundException ex) {
            if (value && !attrs) {
                current."$name" = value
                return current
            }
            else {
                throw ex
            }
        }
    }

    @Override
    protected void nodeCompleted(Object parent, Object node) {
        node.finish()
    }
}

@Canonical
@EqualsAndHashCode(excludes="resolver")
class Votable {
    List<ModelSpec> models = []
    List<DataInstance> dataTypes = []
    List<ObjectInstance> objectTypes = []
    def resolver = new Resolver()

    public setModel(ModelSpec spec) {
        models << spec
        resolver << spec
    }

    public leftShift(DataInstance data) {dataTypes << data}
    public leftShift(ObjectInstance data) {objectTypes << data}

    def init(Map m = [:]) {}
    def finish() {}
}

class Resolver {
    private List<ModelSpec> models = []
    private Map<VodmlRef, Type> types = [:]
    private Map<VodmlRef, Role> roles = [:]

    public Type resolveType(String ref) {
        def key = new VodmlRef(ref)
        return types[key]
    }

    public Role resolveRole(String ref) {
        def key = new VodmlRef(ref)
        return roles[key]
    }

    public VodmlRef resolveAttribute(VodmlRef typeRef, String attributeName) {
        if (roles[attributeName]) {
            return roles[attributeName]
        }
        Type type = types[typeRef]
        def matches = match(type, attributeName)
        if (matches.size() == 1) {
            return new VodmlRef(typeRef.prefix, matches[0].vodmlid)
        }
        else if (matches.size() == 0) {
            throw new IllegalArgumentException(String.format("No Such Attribute '%s' in %s", attributeName, typeRef))
        } else if (matches.size() >1) {
            throw new IllegalArgumentException(String.format("Ambiguous Attribute '%s' in %s", attributeName, typeRef))
        }
    }

    public VodmlRef resolveAttribute(String typeref, String attributeName) {
        VodmlRef typeRef = new VodmlRef(typeref)
        resolveAttribute(typeRef, attributeName)
    }

    public leftShift(ModelSpec spec) {
        models << spec
        index(spec)
    }

    public boolean "extends"(String child, String parent) {
        Type childType = types[new VodmlRef(child)]
        VodmlRef parentRef = new VodmlRef(parent)

        def typeExtends = childType.extends_

        if (typeExtends) {
            def directParent = typeExtends.vodmlref
            if (new VodmlRef(directParent) == parentRef) {
                true
            } else {
                return "extends"(directParent, parent)
            }
        }
    }

    private match(Type type, String attributeName) {
        def matches = ["attributes", "references", "collections"].findResults {
            if (type.hasProperty(it)) {
                type."$it".findResults {
                    if (it.name == attributeName) {
                        it
                    }
                }
            }
        }.flatten()

        if (type.extends_) {
            type = types[new VodmlRef(type.extends_.vodmlref)]
            matches += match(type, attributeName)
        }
        matches
    }

    private void index(ModelSpec spec) {
        indexPackage(spec.name, spec)
        spec.packages.each {
            indexPackage(spec.name, it)
        }
    }

    private indexPackage(String prefix, pkg) {
        ["dataTypes", "objectTypes", "primitiveTypes", "enumerations"].each {
            pkg."$it".each { type ->
                VodmlRef key = new VodmlRef(prefix, type.vodmlid)
                types[key] = type

                ["attributes", "references", "collections"].each {
                    if (type.hasProperty(it)) {
                        type?."$it".each { role ->
                            VodmlRef rkey = new VodmlRef(prefix, role.vodmlid)
                            roles[rkey] = role
                        }
                    }
                }
            }
        }
    }
}

@Canonical(excludes=["resolver", "attrs", "parent"])
abstract class Instance {
    protected attrs
    def parent
    Resolver resolver

    public void init(attrs) {
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

