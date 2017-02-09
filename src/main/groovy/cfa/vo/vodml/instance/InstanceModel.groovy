/*
 * #%L
 * jovial
 * %%
 * Copyright (C) 2016 Smithsonian Astrophysical Observatory
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Smithsonian Astrophysical Observatory nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package cfa.vo.vodml.instance

import cfa.vo.vodml.metamodel.Composition
import cfa.vo.vodml.metamodel.Model
import cfa.vo.vodml.utils.Resolver
import cfa.vo.vodml.utils.VoBuilderNode
import cfa.vo.vodml.utils.VodmlRef
import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode
import groovy.util.logging.Log

@EqualsAndHashCode
class HasObjects {
    Set<ObjectInstance> objectTypes = []

    /**
     * Overloads the left shift operator for adding object type instances to build.
     * It also makes sure the set of buildable strings is updated.
     *
     * @param data the ObjectType instance to be added.
     */
    public leftShift(ObjectInstance data) {
        objectTypes << data
    }
}

@EqualsAndHashCode
class HasColumns {
    Set<ColumnInstance> columns = []

    /**
     * Overloads the left shift operator for adding object type instances to build.
     * It also makes sure the set of buildable strings is updated.
     *
     * @param data the ObjectType instance to be added.
     */
    public leftShift(ColumnInstance data) {
        columns << data
    }
}

@EqualsAndHashCode
class HasAttributes {
    List<AttributeInstance> attributes = []

    /**
     * Overloads the left shift operator for adding data type instances to build.
     * It also makes sure the set of buildable strings is updated.
     *
     * @param data the DataType instance to be added
     */
    public leftShift(AttributeInstance data) {
        attributes << data
    }
}

//@EqualsAndHashCode
//class HasValues {
//    List<LiteralInstance> attributes = []
//
//    /**
//     * Overloads the left shift operator for adding primitive type instances to build.
//     * It also makes sure the set of buildable strings is updated. Instances are single values
//     * or arrays to be serialized atomically (i.e. in a PARAM).
//     *
//     * @param data the PrimitiveType instance to be added
//     */
//    public leftShift(LiteralInstance data) {
//        attributes << data
//    }
//}

@EqualsAndHashCode
class HasReferences {
    List<ReferenceInstance> references = []

    /**
     * Overloads the left shift operator for adding references to build.
     * It also makes sure the set of buildable strings is updated.
     *
     * @param data the Reference instance to be added
     */
    public leftShift(ReferenceInstance data) {
        references << data
    }
}

@EqualsAndHashCode
class HasTables {
    List<TableInstance> tables = []

    /**
     * Overloads the left shift operator for adding references to build.
     * It also makes sure the set of buildable strings is updated.
     *
     * @param data the Reference instance to be added
     */
    public leftShift(TableInstance data) {
        tables << data
    }
}

/**
 * This class provides default implementations to the methods needed by any {@link VoBuilderNode}.
 * It also injects the singleton instance of the {@link Resolver}.
 */
class DefaultNode implements VoBuilderNode {
    Resolver resolver = Resolver.instance
    void start(Map m) {}
    void apply() {}
    void end() {}
}

/**
 * This class represents a Votable instance. Models, ObjectTypes, and DataTypes can be added to it.
 * It is important to provide model specifications (see {@link ModelImportInstance}).
 */
@Canonical
class DataModelInstance extends DefaultNode {
    List<ModelImportInstance> models = []
    @Delegate HasObjects hasObjects = new HasObjects()
    @Delegate HasAttributes hasData = new HasAttributes()
    @Delegate HasTables hasTables = new HasTables()

    /**
     * Overload left shift operator for adding ModelInstances to this votable
     * @param data the ModelImportInstance to add
     * @return
     */
    public leftShift(ModelImportInstance data) {models << data}

    /**
     * Overload left shift operator for adding Models to this votable
     * @param data
     * @return
     */
    public leftShift(Model data) {
        resolver << data}

    @Override
    public boolean equals(Object o) {
        if (! (o instanceof DataModelInstance)) {
            return false
        }
        def other = (DataModelInstance) o
        def ourModels = new HashSet(models)
        def theirModels = new HashSet(other.models)
        def ourObjects = new HashSet(objectTypes)
        def theirObjects = new HashSet(other.objectTypes)
        def ourDataObjects = new HashSet(attributes)
        def theirDataObjects = new HashSet(other.attributes)
        return ourModels.equals(theirModels) && ourObjects.equals(theirObjects) &&
                ourDataObjects.equals(theirDataObjects)
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
abstract class Instance extends DefaultNode {
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

    public VodmlRef getType() {
        if (this.@type) {
            return this.@type
        }
        try {
            return resolver.resolveTypeOfRole(this.@role)?.vodmlref ?: null
        } catch (IllegalArgumentException ignore) {
            return null
        }
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

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Instance)) {
            return false
        }
        if ((this.role != o.role) || (this.type != o.type)) {
            return false
        }
        return true
    }
}

/**
 * Class for DataType instances. These instances can contain other DataType instances as well as
 * References to ObjectTypes and primitive types.
 */
class AttributeInstance extends Instance {
    String value
    @Delegate HasReferences hasReferences = new HasReferences()
    @Delegate HasColumns hasColumns = new HasColumns()
    @Delegate HasAttributes hasAttributes = new HasAttributes()

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof AttributeInstance)) {
            return false
        }
        if (!super.equals(o)) {
            return false
        }
        def ourReferences = new HashSet(references)
        def theirReferences = new HashSet(o.references)
        def ourColumns = new HashSet(columns)
        def theirColumns = new HashSet(o.columns)
        def ourAttributes = new HashSet(o.attributes)
        def theirAttributes = new HashSet(o.attributes)

        return this.value == o.value &&
                ourReferences == theirReferences &&
                ourColumns == theirColumns &&
                ourAttributes == theirAttributes
    }
}

/**
 * Class for instances of ObjectTypes. These instances can contain Collections (Composition relationship) in addition
 * to DataType and PrimitiveType instances.
 */
@Canonical
class ObjectInstance extends Instance {
    @Delegate HasAttributes hasAttributes = new HasAttributes()
    @Delegate HasReferences hasReferences = new HasReferences()
    @Delegate HasColumns hasColumns = new HasColumns()
    List<CompositionInstance> collections = []
    Boolean fullId

    public leftShift(CompositionInstance object) {collections << object}

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof ObjectInstance)) {
            return false
        }
        if (!super.equals(o)) {
            return false
        }
        def ourReferences = new HashSet(references)
        def theirReferences = new HashSet(o.references)
        def ourColumns = new HashSet(columns)
        def theirColumns = new HashSet(o.columns)
        def ourCollections = new HashSet(this.collections)
        def theirCollections = new HashSet(o.collections)

        return ourReferences == theirReferences &&
                ourColumns == theirColumns &&
                ourCollections == theirCollections
    }
}

/**
 * Class implementing instances of Composition relationship. This is simply a container of ObjectTypes.
 */
@Canonical
class CompositionInstance extends Instance {
    @Delegate HasObjects hasObjects = new HasObjects()
    Integer maxOccurs = -1;

    /**
     * Override the {@link HasObjects} trait's left shift operator to propagate the role
     * of the collection to all of the contained objects.
     * @param object
     * @return
     */
    public leftShift(ObjectInstance object) {
        hasObjects.leftShift(object)
        if (object.hasProperty("role") && role != null) {
            object.role = role
        }
    }

    @Override
    public void setRole(String ref) {
        super.setRole(ref)
        Composition comp = resolver.resolveRole(super.role)
        this.maxOccurs = comp.multiplicity.maxOccurs
    }
}

/**
 * Tables are undistinguishable from Collections, but they must be serialized differently
 */
@Canonical
class TableInstance extends CompositionInstance {

}

/**
 * Class for References to ObjectType instances.
 */
@Canonical
class ReferenceInstance extends Instance {
    String value
}

/**
 * Columns are basically references, although differently serialized.
 */
@Canonical
class ColumnInstance extends Instance {
    String value
}

/**
 * Class for model declarations in the VODML preamble. It needs to be passed a {@link Model} specification
 * as well as the URL the model spec will be available at and a documentation URL for the model.
 */
@Log
@EqualsAndHashCode(excludes='spec')
class ModelImportInstance extends Instance {
    String name = ""
    String identifier = ""
    String vodmlURL = ""
    String documentationURL = ""
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

    public void setSpec(Model spec) {
        name = spec.name
        this.spec = spec
    }

    @Override
    public void apply() {
        super.apply()
    }
}
