package cfa.vo.vodml

import cfa.vo.vodml.annotations.Nonnegative
import cfa.vo.vodml.annotations.Required
import groovy.beans.Bindable

import java.text.DateFormat
import java.text.SimpleDateFormat

@Bindable
abstract class ReferableElement {
    @Required String name
    @Required String vodmlid
    String description
}

@Bindable
class ElementRef implements Buildable {
    @Required String vodmlref

    @Override
    void build(GroovyObject builder) {
        def elem = {
            "extends"() {
                "vodml-ref"(this.vodmlref)
            }
        }
        elem.delegate = builder
        elem()
    }
}

@Bindable
abstract class Type extends ReferableElement {
    ElementRef extends_
    List<Constraint> constraints
}

@Bindable
abstract class ValueType extends Type {
}

@Bindable
class PrimitiveType extends ValueType implements Buildable {
    @Override
    void build(GroovyObject builder) {
        def elem = {
            primitiveType() {
                name(this.name)
                "vodml-id"(this.vodmlid)
                description(this.description)
                out << this.extends_
            }
        }
        elem.delegate = builder
        elem()
    }
}

@Bindable
class Enumeration_ extends ValueType implements Buildable {
    List<EnumLiteral> literals

    @Override
    void build(GroovyObject builder) {
        def elem = {
            enumeration() {
                name(this.name)
                "vodml-id"(this.vodmlid)
                description(this.description)
                out << this.extends_
                this.literals.each {
                    out << it
                }
            }
        }
        elem.delegate = builder
        elem()
    }
}

@Bindable
class EnumLiteral extends ReferableElement implements Buildable {
    @Override
    void build(GroovyObject builder) {
        def elem = {
            literal() {
                name(this.name)
                "vodml-id"(this.vodmlid)
                description(this.description)
            }
        }
        elem.delegate = builder
        elem()
    }
}

@Bindable
class DataType extends ValueType implements Buildable {
    List<Attribute> attributes
    List<Reference> references

    @Override
    void build(GroovyObject builder) {
        def elem = {
            attributes.each {
                out << it
            }
            references.each {
                out << it
            }
        }
    }
}

@Bindable
class ObjectType extends Type implements Buildable {
    List<Attribute> attributes
    List<Composition> collections
    List<Reference> references

    @Override
    void build(GroovyObject builder) {
        def elem = {
            objectType() {
                name(this.name)
                "vodml-id"(this.vodmlid)
                description(this.description)
                out << this.extends_
                this.attributes.each {
                    out << it
                }
                this.collections.each {
                    out << it
                }
                this.references.each {
                    out << it
                }
            }
        }
        elem.delegate = builder
        elem()
    }
}

@Bindable
abstract class Role extends ReferableElement {
    ElementRef dataType
    Multiplicity multiplicity = new Multiplicity()
}

@Bindable
class Attribute extends Role implements Buildable {
    List<SemanticConcept> semanticConcepts

    @Override
    void build(GroovyObject builder) {
        def elem = {
            attribute() {
                name(this.name)
                "vodml-id"(this.vodmlid)
                description(this.description)
                semanticConcepts.each {
                    out << it
                }
            }
        }
        elem.delegate = builder
        elem()
    }
}

@Bindable
class SemanticConcept implements Buildable {
    URI vocabularyURI
    URI topConcept

    @Override
    void build(GroovyObject builder) {
        def elem = {
            vocabularyURI(this.vocabularyURI)
            topConcept(this.topConcept)
        }
        elem.delegate = builder
        elem()
    }
}

@Bindable
abstract class Relation extends Role {

}

@Bindable
class Composition extends Relation implements Buildable {
    @Override
    void build(GroovyObject builder) {
        def elem = {
            relation() {
                name(this.name)
                "vodml-id"(this.vodmlid)
                description(this.description)
                out << this.dataType
                out << this.multiplicity
            }
        }
        elem.delegate = builder
        elem()
    }
}

@Bindable
class Reference extends Relation implements Buildable {
    @Override
    void build(GroovyObject builder) {
        def elem = {
            relation() {
                name(this.name)
                "vodml-id"(this.vodmlid)
                description(this.description)
                out << this.dataType
                out << this.multiplicity
            }
        }
        elem.delegate = builder
        elem()
    }
}

@Bindable
class Multiplicity implements Buildable {
    @Nonnegative Integer minOccurs = 1
    @Nonnegative Integer maxOccurs = 1

    @Override
    void build(GroovyObject builder) {
        def elem = {
            multiplicity() {
                minOccurs(this.minOccurs)
                maxOccurs(this.maxOccurs)
            }
        }
        elem.delegate = builder
        elem()
    }
}

@Bindable
class Constraint extends ReferableElement implements Buildable {
    @Override
    void build(GroovyObject builder) {
        def elem = {
            constraint() {
                name(this.name)
                "vodml-id"(this.vodmlid)
                description(this.description)
            }
        }
        elem.delegate = builder
        elem()
    }
}

@Bindable
class Package extends ReferableElement implements Buildable {
    List<ObjectType> objectTypes
    List<DataType> dataTypes
    List<PrimitiveType> primitiveTypes
    List<Enumeration_> enumerations
    List<Package> packages

    @Override
    void build(GroovyObject builder) {
        def package_ = {
            "package"() {
                name(this.name)
                "vodml-id"(this.vodmlid)
                description(this.description)
                this.packages.each {
                    out << it
                }
                this.objectTypes.each {
                    out << it
                }
                this.dataTypes.each {
                    out << it
                }
                this.enumerations.each {
                    out << it
                }
                this.primitiveTypes.each {
                    out << it
                }
            }
        }
        package_.delegate = builder
        package_()
    }
}

@Bindable
class Model implements Buildable {
    String prefix = "vo-dml"
    String ns = "http://www.ivoa.net/xml/VODML/v1.0"
    @Required String name = "my:model"
    @Required String title = "My Model"
    @Required String version = "1.0"
    @Required Date lastModified = new Date()
    String description
    List<String> authors
    List<URI> previousVersions
    List<ModelImport> imports
    List<Package> packages
    List<ObjectType> objectTypes
    List<DataType> dataTypes
    List<Enumeration_> enumerations
    List<PrimitiveType> primitiveTypes

    static Model from(xml) {

    }

    @Override
    void build(GroovyObject builder) {
        def model = {
            "vo-dml:model"("xsi:schemaLocation": "${this.ns} http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/xsd/vo-dml-v1.0.xsd") {
                name(this.name)
                title(this.title)
                version(this.version)
                lastModified(format(this.lastModified))
                description(this.description)
                this.authors.each {
                    author(it)
                }
                this.previousVersions.each {
                    previousVersion(it)
                }
                this.imports.each {
                    out << it
                }
                this.packages.each {
                    out << it
                }
                this.objectTypes.each {
                    out << it
                }
                this.dataTypes.each {
                    out << it
                }
                this.enumerations.each {
                    out << it
                }
                this.primitiveTypes.each {
                    out << it
                }
            }
        }
        model.delegate = builder
        model()
    }

    private String format(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        df.format(date);
    }
}

@Bindable
class ModelImport implements Buildable {
    @Required String name
    @Required String version
    @Required URL url
    @Required URL documentationURL

    @Override
    void build(GroovyObject builder) {
        def modimp = {
            "import"() {
                name(this.name)
                version(this.version)
                url(this.url)
                documentationURL(this.documentationURL)
            }
        }
        modimp.delegate = builder
        modimp()
    }
}
