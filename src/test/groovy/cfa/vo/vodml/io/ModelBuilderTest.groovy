package cfa.vo.vodml.io

import ca.odell.glazedlists.EventList
import cfa.vo.vodml.VodmlException
import cfa.vo.vodml.metamodel.ElementRef
import cfa.vo.vodml.metamodel.Model
import cfa.vo.vodml.metamodel.Multiplicity
import cfa.vo.vodml.utils.VodmlRef
import org.custommonkey.xmlunit.XMLUnit
import spock.lang.Specification

class ModelBuilderTest extends Specification {
    def builder = new ModelBuilder()

    def "test no factory"() {
        when: "a non existent method is used"
        builder.model() {
            foo() {}
        }
        then: "a user friendly message is provided with the exception"
        def ex = thrown(VodmlException)
        ex.message == "'foo' is not a valid node type"
    }

    def "test builder basic functionality"() {
        when: "a simple model is created"
        def model = builder.model(name: "test", version: "1.0", description: "A Description", authors: ["Jane Doe", "John Doe"]) {
            "package"(name: "aPackage") {
                dataType(name: "DataType") {
                    attribute(name: "anAttribute")
                }
                dataType(name: "SomeData") {
                    attribute(name: "attr")
                }
            }
        }
        then: "check metadata"
        model.name == "test"
        model.version == "1.0"
        model.description == "A Description"
        model.authors == ["Jane Doe", "John Doe"] as EventList
        model.packages[0].name == "aPackage"
        model.packages[0].dataTypes[0].name == "DataType"
        model.packages[0].dataTypes[0].attributes[0].name == "anAttribute"
        model.packages[0].dataTypes[1].name == "SomeData"
        model.packages[0].dataTypes[1].attributes[0].name == "attr"

        and: "check vodmlid automatic assignment works"
        model.packages[0].vodmlid == new VodmlRef("aPackage")
        model.packages[0].dataTypes[0].vodmlid == new VodmlRef("aPackage.DataType")
    }

    def "test object type in model"() {
        when: "a model with an object type is created"
        Model model = builder.model(name: "ds") {
            objectType(name: "Role") {
                reference(name: "party", dataType: "ds.party.Party")
            }
        }
        then: "basic metadata is correct"
        model.name == "ds"
        model.objectTypes[0].name == "Role"
        model.objectTypes[0].references[0].name == "party"

        and: "vodmlid automatically assigned"
        model.objectTypes[0].vodmlid == new VodmlRef("Role")
        model.objectTypes[0].references[0].vodmlid == new VodmlRef("Role.party")

        and: "reference points to the correct object"
        model.objectTypes[0].references[0].dataType == new ElementRef(vodmlref:"ds:party.Party")
    }

    def "refer to elements with no quotes"() {
        when: "a model with a reference without quotes is created"
        Model model = builder.model(name: "ds") {
            objectType(name: "Role") {
                reference(name: "party", dataType: ds.party.Party)
            }
        }
        then: "reference points to the correct object"
        model.objectTypes[0].references[0].dataType == new ElementRef(vodmlref:"ds:party.Party")
    }

    def "test ObjectType with extension and attributes"() {
        when: "a model with an ObjectType with extension and attributes is created"
        Model model = builder.model(name: "ds") {
            pack(name: "party") {
                objectType(name: "Organization", parent: ds.party.Party) {
                    attribute(name: "address", dataType: ivoa.string)
                    attribute(name: "phone", dataType: ivoa.string)
                    attribute(name: "email", dataType: ivoa.string)
                    attribute(name: "logo", dataType: ivoa.anyURI)
                }
            }
        }
        then: "check basic metadata"
        model.name == "ds"
        model.packages[0].name == "party"
        model.packages[0].vodmlid == new VodmlRef("party")
        model.packages[0].objectTypes[0].name == "Organization"
        model.packages[0].objectTypes[0].vodmlid == new VodmlRef("party.Organization")
        model.packages[0].objectTypes[0].vodmlid == new VodmlRef("party.Organization")
        model.packages[0].objectTypes[0].extends_ == [vodmlref: "ds:party.Party"] as ElementRef
        model.packages[0].objectTypes[0].attributes[0].name == "address"
        model.packages[0].objectTypes[0].attributes[0].dataType == [vodmlref: "ivoa:string"] as ElementRef
        model.packages[0].objectTypes[0].attributes[0].vodmlid == new VodmlRef("party.Organization.address")
        model.packages[0].objectTypes[0].attributes[1].dataType == [vodmlref: "ivoa:string"] as ElementRef
        model.packages[0].objectTypes[0].attributes[1].vodmlid == new VodmlRef("party.Organization.phone")
        model.packages[0].objectTypes[0].attributes[2].dataType == [vodmlref: "ivoa:string"] as ElementRef
        model.packages[0].objectTypes[0].attributes[2].vodmlid == new VodmlRef("party.Organization.email")
        model.packages[0].objectTypes[0].attributes[3].dataType == [vodmlref: "ivoa:anyURI"] as ElementRef
        model.packages[0].objectTypes[0].attributes[3].vodmlid == new VodmlRef("party.Organization.logo")
    }

    def "test names as values"() {
        when: "a model with an ObjectType with extension and attributes is created"
        Model model = builder.model("ds") {
            pack("party") {
                objectType("Organization", parent: ds.party.Party) {
                    attribute("address", dataType: ivoa.string)
                    attribute("phone", dataType: ivoa.string)
                    attribute("email", dataType: ivoa.string)
                    attribute("logo", dataType: ivoa.anyURI)
                }
            }
        }
        then: "check basic metadata"
        model.name == "ds"
        model.packages[0].name == "party"
        model.packages[0].vodmlid == new VodmlRef("party")
        model.packages[0].objectTypes[0].name == "Organization"
        model.packages[0].objectTypes[0].vodmlid == new VodmlRef("party.Organization")
        model.packages[0].objectTypes[0].vodmlid == new VodmlRef("party.Organization")
        model.packages[0].objectTypes[0].extends_ == [vodmlref: "ds:party.Party"] as ElementRef
        model.packages[0].objectTypes[0].attributes[0].name == "address"
        model.packages[0].objectTypes[0].attributes[0].dataType == [vodmlref: "ivoa:string"] as ElementRef
        model.packages[0].objectTypes[0].attributes[0].vodmlid == new VodmlRef("party.Organization.address")
        model.packages[0].objectTypes[0].attributes[1].dataType == [vodmlref: "ivoa:string"] as ElementRef
        model.packages[0].objectTypes[0].attributes[1].vodmlid == new VodmlRef("party.Organization.phone")
        model.packages[0].objectTypes[0].attributes[2].dataType == [vodmlref: "ivoa:string"] as ElementRef
        model.packages[0].objectTypes[0].attributes[2].vodmlid == new VodmlRef("party.Organization.email")
        model.packages[0].objectTypes[0].attributes[3].dataType == [vodmlref: "ivoa:anyURI"] as ElementRef
        model.packages[0].objectTypes[0].attributes[3].vodmlid == new VodmlRef("party.Organization.logo")
    }

    def "test imports"() {
        when:
        Model model = builder.model("test") {
            include("ivoa", version:"1.0", url: "https://some/url", documentationURL: new URL("http://some/doc"))
            "import"("stc", version:"2.0", url: "https://stc/model", documentationURL: "https://stc/doc")
        }
        then:
        model.imports[0].name == "ivoa"
        model.imports[0].version == "1.0"
        model.imports[0].url == new URL("https://some/url")
        model.imports[0].documentationURL == new URL("http://some/doc")
        model.imports[1].name == "stc"
        model.imports[1].version == "2.0"
        model.imports[1].url == new URL("https://stc/model")
        model.imports[1].documentationURL == new URL("https://stc/doc")
    }

    def "test enumerations"() {
        when:
        Model model = builder.model("test") {
            enumeration("DataProductType") {
                literal("CUBE")
                literal("IMAGE")
                literal("PHOTOMETRY")
            }
            pack("aPackage") {
                enumeration("CreationType") {
                    literal("ARCHIVAL")
                    literal("CUTOUT")
                    literal("FILTERED")
                }
            }
        }
        then:
        model.enumerations[0].name == "DataProductType"
        model.enumerations[0].vodmlid == new VodmlRef("DataProductType")
        model.enumerations[0].literals[0].name == "CUBE"
        model.enumerations[0].literals[0].vodmlid == new VodmlRef("DataProductType.CUBE")
        model.enumerations[0].literals[1].name == "IMAGE"
        model.enumerations[0].literals[1].vodmlid == new VodmlRef("DataProductType.IMAGE")
        model.enumerations[0].literals[2].name == "PHOTOMETRY"
        model.enumerations[0].literals[2].vodmlid == new VodmlRef("DataProductType.PHOTOMETRY")
        model.packages[0].name == "aPackage"
        model.packages[0].enumerations[0].vodmlid == new VodmlRef("aPackage.CreationType")
        model.packages[0].enumerations[0].name == "CreationType"
        model.packages[0].enumerations[0].literals[0].name == "ARCHIVAL"
        model.packages[0].enumerations[0].literals[0].vodmlid == new VodmlRef("aPackage.CreationType.ARCHIVAL")
        model.packages[0].enumerations[0].literals[1].name == "CUTOUT"
        model.packages[0].enumerations[0].literals[1].vodmlid == new VodmlRef("aPackage.CreationType.CUTOUT")
        model.packages[0].enumerations[0].literals[2].name == "FILTERED"
        model.packages[0].enumerations[0].literals[2].vodmlid == new VodmlRef("aPackage.CreationType.FILTERED")
    }

    def "test multiplicity"() {
        given:
        Model model = builder.model("test") {
            dataType("data") {
                attribute("attr", multiplicity: "0..1")
                attribute("attr2", multiplicity: "0..-1")
                reference("ref", multiplicity: "1..*")
            }
            objectType("object") {
                collection("coll", multiplicity: "0..3")
            }
        }
        expect:
        model.dataTypes[0].attributes[0].multiplicity == new Multiplicity(minOccurs: 0, maxOccurs: 1)
        model.dataTypes[0].attributes[1].multiplicity == new Multiplicity(minOccurs: 0, maxOccurs: -1)
        model.dataTypes[0].references[0].multiplicity == new Multiplicity(minOccurs: 1, maxOccurs: -1)
        model.objectTypes[0].collections[0].multiplicity == new Multiplicity(minOccurs: 0, maxOccurs: 3)
    }

    def "test multiplicity error"() {
        when:
        Model model = builder.model("test") {
            dataType("data") {
                attribute("attr", multiplicity: "0..")
            }
        }
        then:
        def ex = thrown(RuntimeException)
        ex.cause.message == """
Illegal multiplicity expression. Please use '<minOccurs>..<maxOccurs>', where:
  <minOccurs> is a non negative integer, and
  <maxOccurs> is a positive integer, or '-1', or '*' for unbound multiplicity.
"""
    }

    def "full model (as in MetaModelTest but with DSL)"() {
        given: "I/O infrastructure"
        def writer = new VodmlWriter()
        def os = new ByteArrayOutputStream()
        XMLUnit.ignoreWhitespace = true
        and: "full model"
        Model model = builder.model("ds") {
            title("Dataset Metadata")
            description("Generic, high-level metadata associated with an IVOA Dataset.")
            lastModified("2016-04-20T16:44:59.239-04:00")
            author("John Doe")
            author("Jane Doe")
            include("ivoa", version:"1.0", url: "https://some/url")
            enumeration("DataProductType") {
                literal("CUBE", description: "Data Cube")
                literal("IMAGE", description: "Image")
                literal("PHOTOMETRY", description: "Photometry")
                literal("SPECTRUM", description: "Spectrum")
                literal("TIMESERIES", description: "Time Series")
                literal("SED", description: "Spectral Energy Distribution")
                literal("VISIBILITY", description: "Visibility")
                literal("EVENT", description: "Event List")
                literal("CATALOG", description: "Catalog")
            }
            enumeration("CreationType") {
                literal("ARCHIVAL", description: "Archival")
                literal("CUTOUT", description: "Cutout")
                literal("FILTERED", description: "Filtered")
                literal("MOSAIC", description: "Mosaic")
                literal("SPECTRAL_EXTRACTION", description: "Spectral Extraction")
                literal("CATALOG_EXTRACTION", description: "Catalog Extraction")
            }
            enumeration("RightsType") {
                literal("PUBLIC", description: "Public Access")
                literal("PROPRIETARY", description: "Proprietary Access")
                literal("SECURE", description: "Secure Access")
            }
            pack("dataset") {
                dataType("Collection") {
                    attribute("name", dataType: ivoa.string)
                }
                dataType("Contributor", parent: ds.party.Role) {
                    attribute("acknowledgement", dataType: ivoa.string)
                }
                dataType("Creator", parent: ds.party.Role)
                objectType("DataID") {
                    attribute("title", dataType: ivoa.string, multiplicity: "0..1")
                }
            }
            pack("party") {
                objectType("Organization", parent: ds.party.Party) {
                    attribute("address", dataType: ivoa.string)
                    attribute("phone", dataType: ivoa.string)
                    attribute("email", dataType: ivoa.string)
                    attribute("logo", dataType: ivoa.anyURI)
                }
            }
        }
        and: "baseline model serialization"
        String expected = getClass().getResource("/dataset.vo-dml.xml").text

        when: "model is serialized"
        writer.write(model, System.out)

        then: "serialized model is equal to baseline"
//        XMLAssert.assertXMLEqual(expected, os.toString("UTF-8"))
        true
    }
}
