package cfa.vo.vodml

import cfa.vo.vodml.annotations.Nonnegative
import cfa.vo.vodml.annotations.Required
import groovy.beans.Bindable
import org.joda.time.DateTime

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
            "vodml-ref"(this.vodmlref)
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
                this.constraints.each {
                    out << it
                }
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
                "vodml-id"(this.vodmlid)
                name(this.name)
                description(this.description)
                if (this.extends_) {
                    "extends"() {
                        out << this.extends_
                    }
                }
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
                "vodml-id"(this.vodmlid)
                name(this.name)
                description(this.description)
            }
        }
        elem.delegate = builder
        elem()
    }
}

@Bindable
class DataType extends ValueType implements Buildable {
    def abstract_ = false
    List<Attribute> attributes
    List<Reference> references

    @Override
    void build(GroovyObject builder) {
        def args = []
        if (abstract_) {
            args = ["abstract": true]
        }
        def elem = {
            dataType(args) {
                "vodml-id"(this.vodmlid)
                name(this.name)
                description(this.description)
                if (extends_) {
                    "extends"(this.extends_)
                }
                this.constraints.each {
                    out << it
                }
                attributes.each {
                    out << it
                }
                references.each {
                    out << it
                }
            }
        }
        elem.delegate = builder
        elem()
    }
}

@Bindable
class ObjectType extends Type implements Buildable {
    def abstract_ = false
    List<Attribute> attributes
    List<Composition> collections
    List<Reference> references

    @Override
    void build(GroovyObject builder) {
        def elem = {
            def args = []
            if (abstract_) {
                args = ["abstract": true]
            }
            objectType(args) {
                "vodml-id"(this.vodmlid)
                name(this.name)
                description(this.description)
                if (this.extends_) {
                    "extends"() {
                        out << this.extends_
                    }
                }
                this.constraints.each {
                    out << it
                }
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
                "vodml-id"(this.vodmlid)
                name(this.name)
                description(this.description)
                datatype() {
                    out << this.dataType
                }
                out << this.multiplicity
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
    String vocabularyURI
    String topConcept

    @Override
    void build(GroovyObject builder) {
        def elem = {
            if (topConcept || vocabularyURI) {
                semanticconcept() {
                    if (topConcept) {
                        topConcept(new URI(this.topConcept))
                    }
                    if (vocabularyURI) {
                        vocabularyURI(new URI(this.vocabularyURI))
                    }
                }
            }
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
            collection() {
                "vodml-id"(this.vodmlid)
                name(this.name)
                description(this.description)
                datatype() {
                    out << this.dataType
                }
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
            reference() {
                "vodml-id"(this.vodmlid)
                name(this.name)
                description(this.description)
                datatype() {
                    out << this.dataType
                }
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
                description(this.description)
            }
        }
        elem.delegate = builder
        elem()
    }
}

@Bindable
class SubsettedRole extends Constraint {
    ElementRef role
    ElementRef dataType
    SemanticConcept semanticConcept

    @Override
    void build(GroovyObject builder) {
        def elem = {
            constraint("xsi:type": "vo-dml:SubsettedRole") {
                role() {
                    out << this.role
                }
                datatype {
                    out << this.dataType
                }
                if (this.semanticConcept) {
                    out << semanticConcept
                }
            }
        }
        elem.delegate = builder
        elem()
    }
}

@Bindable
class Package extends ReferableElement implements Buildable {
    List<PrimitiveType> primitiveTypes
    List<Enumeration_> enumerations
    List<DataType> dataTypes
    List<ObjectType> objectTypes
    List<Package> packages

    @Override
    void build(GroovyObject builder) {
        def package_ = {
            "package"() {
                "vodml-id"(this.vodmlid)
                name(this.name)
                description(this.description)
                this.primitiveTypes.each {
                    out << it
                }
                this.enumerations.each {
                    out << it
                }
                this.dataTypes.each {
                    out << it
                }
                this.objectTypes.each {
                    out << it
                }
                this.packages.each {
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
    @Required DateTime lastModified = new DateTime()
    String description
    List<String> authors = []
    List<URI> previousVersions = []
    List<ModelImport> imports = []
    List<PrimitiveType> primitiveTypes = []
    List<Enumeration_> enumerations = []
    List<DataType> dataTypes = []
    List<ObjectType> objectTypes = []
    List<Package> packages = []

    @Override
    void build(GroovyObject builder) {
        def model = {
            "vo-dml:model"("xsi:schemaLocation": "${this.ns} http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/xsd/vo-dml-v1.0.xsd") {
                name(this.name)
                description(this.description)
                title(this.title)
                version(this.version)
                lastModified(this.lastModified)
                this.authors.each {
                    author(it)
                }
                this.previousVersions.each {
                    previousVersion(it)
                }
                this.imports.each {
                    out << it
                }
                this.primitiveTypes.each {
                    out << it
                }
                this.enumerations.each {
                    out << it
                }
                this.dataTypes.each {
                    out << it
                }
                this.objectTypes.each {
                    out << it
                }
                this.packages.each {
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
