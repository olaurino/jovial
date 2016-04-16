package cfa.vo.vodml

import cfa.vo.vodml.annotations.Nonnegative
import cfa.vo.vodml.annotations.Required
import groovy.beans.Bindable

@Bindable
class ReferableElement {
    @Required String name
    @Required String vodmlid
    String description
}

@Bindable
class ElementRef {
    @Required String vodmlref
}

@Bindable
class Type extends ReferableElement {
    ElementRef extends_
    Constraint constraint
}

@Bindable
class ValueType extends Type {

}

@Bindable
class PrimitiveType extends ValueType {

}

@Bindable
class Enumeration_ extends ValueType {
    List<EnumLiteral> literals
}

@Bindable
class EnumLiteral extends ReferableElement {

}

@Bindable
class DataType extends ValueType {
    List<Attribute> attributes
    List<Reference> reference
}

@Bindable
class ObjectType extends Type {
    List<Attribute> attributes
    List<Composition> collections
    List<Reference> references
}

@Bindable
class Role extends ReferableElement {
    ElementRef dataType
    Multiplicity multiplicity
}

@Bindable
class Attribute extends Role {
    List<SemanticConcept> semanticConcepts
}

@Bindable
class SemanticConcept {
    URI vocabularyURI
    URI topConcept
}

@Bindable
class Relation extends Role {

}

@Bindable
class Composition extends Relation {

}

@Bindable
class Reference extends Relation {

}

@Bindable
class Multiplicity {
    @Nonnegative Integer minOccurs = 1
    @Nonnegative Integer maxOccurs = 1
}

@Bindable
class Constraint extends ReferableElement {

}

@Bindable
class Package extends ReferableElement {
    List<ObjectType> objectTypes
    List<DataType> dataTypes
    List<PrimitiveType> primitiveTypes
    List<Enumeration_> enumerations
    List<Package> packages
}

@Bindable
class Model {
    @Required String name = "my:model"
    @Required String title = "My Model"
    @Required String version = "1.0"
    @Required Date lastModified = Date()
    String description
    List<String> authors
    List<URI> previousVersions
    List<ModelImport> imports
    List<Package> packages
    List<ObjectType> objectTypes
    List<DataType> dataTypes
    List<Enumeration_> enumerations
    List<PrimitiveType> primitiveTypes
}

@Bindable
class ModelImport {
    @Required String name
    @Required String version
    @Required URL url
    @Required URL documentationURL
}
