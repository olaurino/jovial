package cfa.vo.vodml.instance

import cfa.vo.vodml.Model
import cfa.vo.vodml.io.VoTableBuilder
import cfa.vo.vodml.io.VodmlWriter
import cfa.vo.vodml.utils.Resolver
import cfa.vo.vodml.utils.VoBuilderNode
import cfa.vo.vodml.utils.VodmlRef
import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode
import groovy.util.logging.Log

/**
 * A Groovy Trait that knows how to build its own collections.
 *
 * The assumption is that each buildable node will have one or more lists of attributes,
 * each with its own buildable implementation.
 *
 * Implementing classes can call the build closure in their build implementation and pass
 * their own delegate to it. The closure will build the required attributes.
 *
 * The set of attribute lists is updated by calling the {@link VodmlBuildable#add(String)} method.
 *
 * The trait also defines an abstract callback method that will be called by this trait's
 * default implementation of the {@link VodmlBuildable#build(groovy.lang.GroovyObject)} method.
 */
trait VodmlBuildable implements Buildable {
    Set<String> toBuild = []

    /**
     * Add a field name to the set of field names to be built by this buildable instance
     * @param name
     */
    void add(String name) {
        toBuild << name
    }

    /**
     * Convenience closure implementing classes can use to have their field lists built
     */
    Closure build = { delegate ->
        toBuild.each { arrayToBuild ->
            this."$arrayToBuild".each {
                delegate.out << it
            }
        }
    }

    /**
     * Default implementation of the Buildable interface. It sets up the correct delegate
     * for the callback concrete implementation and then calls it
     * @param builder The builder object injected by the markup builder.
     */
    @Override
    void build(GroovyObject builder) {
        def callback = getBuildCallback()
        callback.delegate = builder
        callback.call()
    }

    /**
     * Implementing classes must implement this method and return their build callback
     * @return The callback that builds this buildable instance.
     */
    abstract Closure getBuildCallback()
}

/**
 * Trait for buildable nodes that have a list of ObjectType-s to build.
 */
trait HasObjects implements VodmlBuildable {
    List<ObjectInstance> objectTypes = []

    /**
     * Overloads the left shift operator for adding object type instances to build.
     * It also makes sure the set of buildable strings is updated.
     *
     * @param data the ObjectType instance to be added.
     */
    public leftShift(ObjectInstance data) {
        objectTypes << data
        add("objectTypes")
    }
}

/**
 * Trait for buildable nodes that have a list of DataTypes to build
 */
trait HasData implements VodmlBuildable {
    List<DataInstance> dataTypes = []

    /**
     * Overloads the left shift operator for adding data type instances to build.
     * It also makes sure the set of buildable strings is updated.
     *
     * @param data the DataType instance to be added
     */
    public leftShift(DataInstance data) {
        dataTypes << data
        add("dataTypes")
    }
}

/**
 * Trait for buildable nodes that have a list of primitive values to build
 */
trait HasValues implements VodmlBuildable {
    List<ValueInstance> attributes = []

    /**
     * Overloads the left shift operator for adding primitive type instances to build.
     * It also makes sure the set of buildable strings is updated. Instances are single values
     * or arrays to be serialized atomically (i.e. in a PARAM).
     *
     * @param data the PrimitiveType instance to be added
     */
    public leftShift(ValueInstance data) {
        attributes << data
        toBuild.add("attributes")
    }
}

/**
 * Trait for buildable nodes that have a list of references to build
 */
trait HasReferences implements VodmlBuildable {
    List<ReferenceInstance> references = []

    /**
     * Overloads the left shift operator for adding references to build.
     * It also makes sure the set of buildable strings is updated.
     *
     * @param data the Reference instance to be added
     */
    public leftShift(ReferenceInstance data) {
        references << data
        add("references")
    }
}

/**
 * This trait provides default implementations to the methods needed by any {@link VoBuilderNode}.
 * It also injects the singleton instance of the {@link Resolver}.
 */
trait DefaultNode implements VoBuilderNode {
    Resolver resolver = Resolver.instance
    void start(Map m = [:]) {}
    void apply() {}
    void end() {}
}

/**
 * This class represents a Votable instance. Models, ObjectTypes, and DataTypes can be added to it.
 * It is important to provide model specifications (see {@link ModelInstance}).
 */
@Canonical
@EqualsAndHashCode(excludes="resolver")
class VotableInstance implements DefaultNode, HasObjects, HasData {
    String ns = "http://www.ivoa.net/xml/VOTable/v1.2"
    String prefix = ""
    List<ModelInstance> models = []

    /**
     * Overload left shift operator for adding ModelInstances to this votable
     * @param data the ModelInstance to add
     * @return
     */
    public leftShift(ModelInstance data) {models << data}

    /**
     * Overload left shift operator for adding Models to this votable
     * @param data
     * @return
     */
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

/**
 * Base classe for instances. It provides overloaded methods for accepting both Strings and {@link VodmlRef}s
 * as type and roles. It provides default implementations for the node creation lifecycle {@link Instance#start(Map)},
 * {@link Instance#apply()}, and {@link Instance#end()}.
 *
 * When setting a role for this instance, one can use both the full qualified {@link VodmlRef} or just the
 * role's name. The name will we resolved to the fully qualified {@link VodmlRef}.
 */
@Canonical(excludes=["resolver", "attrs", "parent"])
abstract class Instance implements DefaultNode {
    String id
    def parent
    VodmlRef type
    VodmlRef role
    protected Map attrs

    /**
     * Overloads type setter for automatically get a {@link VodmlRef} from a String.
     * @param ref
     */
    void setType(String ref) {
        type = new VodmlRef(ref)
    }

    /**
     * Overloads role setter for automatically get a {@link VodmlRef} from a String. Moreover, the
     * passed String can be either a fully qualified role reference, or an attribute name. The attribute
     * name will be resolved by looking up the list of roles of the parent object.
     * @param ref A String representing wither the fully qualified role {@link VodmlRef} or an attribute name
     *    of the enclosing object.
     */
    void setRole(String ref) {
        try {
            this.role = this.resolver.resolveAttribute(this.parent.type, ref)
        } catch (Exception ex) {
            this.role = new VodmlRef(ref)
        }
    }

    @Override
    public void start(Map attrs) {
        this.attrs = attrs
    }

    @Override
    public void apply() {
        attrs.each {k, v -> this."$k" = v}
    }

    @Override
    public void end() {

    }
}

/**
 * Class for DataType instances. These instances can contain other DataType instances as well as
 * References to ObjectTypes and primitive types.
 */
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
            GROUP(m) {
                out << new Vodml(vodmlm)
                build.call(delegate)
            }
        }
    }
}

/**
 * Class for instances of ObjectTypes. These instances can contain Collections (Composition relationship) in addition
 * to DataType and PrimitiveType instances.
 */
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

/**
 * Class implementing instances of Composition relationship. This is simply a container of ObjectTypes.
 */
@Canonical
class CollectionInstance extends Instance implements HasObjects {

    /**
     * Override the {@link HasObjects} trait's left shift operator to propagate the role
     * of the collection to all of the contained objects.
     * @param object
     * @return
     */
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

/**
 * Class for References to ObjectType instances.
 */
@Canonical
class ReferenceInstance extends Instance implements VodmlBuildable {
    String value

    @Override
    Closure getBuildCallback() {
        def elem = {
            if (value != null) {
                GROUPref(ref: value) {
                    out << new Vodml(role: role)
                }
            }
        }
    }
}

/**
 * Class for values. It supports arrays, but it is meant to be represented by atomic elements (i.e. PARAMs).
 *
 * The class attempts to infer the datatype of the value being passed.
 */
@Canonical(includes=["type","role"])
class ValueInstance extends Instance implements VodmlBuildable {
    def value

    @Override
    Closure getBuildCallback() {
        def elem = {
            if (value != null) {
                PARAM(paramAttrs()) {
                    out << new Vodml(role: role)
                }
            }
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
            } else if (value.respondsTo("size")) {
                ars = value.size().toString()
            }
        }

        [datatype: dt, arraysize: ars]
    }
}

/**
 * Class for model declarations in the VODML preamble. It needs to be passed a {@link Model} specification
 * as well as the URL the model spec will be available at and a documentation URL for the model.
 */
@Log
class ModelInstance extends Instance implements VodmlBuildable {
    String vodmlURL
    String documentationURL
    Model spec

    /**
     * Overrides parent's end() method for making sure parent receives the model spec.
     */
    @Override
    void end() {
        super.end()
        try {
            parent << spec
        } catch(Exception ex) {
            log.warning("Could not pass $spec up to parent $parent")
        }
    }

    @Override
    Closure getBuildCallback() {
        def object = new VoTableBuilder().object(type: "vo-dml:Model") {
            value(role: "vo-dml:Model.name", value:spec.name)
            value(role: "vo-dml:Model.version", value: spec.version)
            value(role: "vo-dml:Model.url", value: vodmlURL)
            value(role: "vo-dml:Model.documentationURL", value: documentationURL)
        }
        def elem = {
            out << object
        }
    }
}

/**
 * Class implementing the VODML element and its serialization.
 */
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

