package cfa.vo.vodml.io

import ca.odell.glazedlists.EventList
import cfa.vo.vodml.VodmlException
import cfa.vo.vodml.metamodel.ElementRef
import cfa.vo.vodml.metamodel.Model
import cfa.vo.vodml.utils.VodmlRef
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
        model.packages[0].vodmlid == new VodmlRef("test:aPackage")
        model.packages[0].dataTypes[0].vodmlid == new VodmlRef("test:aPackage.DataType")
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
        model.objectTypes[0].vodmlid == new VodmlRef("ds:Role")
        model.objectTypes[0].references[0].vodmlid == new VodmlRef("ds:Role.party")

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
        model.packages[0].vodmlid == new VodmlRef("ds:party")
        model.packages[0].objectTypes[0].name == "Organization"
        model.packages[0].objectTypes[0].vodmlid == new VodmlRef("ds:party.Organization")
        model.packages[0].objectTypes[0].vodmlid == new VodmlRef("ds:party.Organization")
        model.packages[0].objectTypes[0].extends_ == [vodmlref: "ds:party.Party"] as ElementRef
        model.packages[0].objectTypes[0].attributes[0].name == "address"
        model.packages[0].objectTypes[0].attributes[0].dataType == [vodmlref: "ivoa:string"] as ElementRef
        model.packages[0].objectTypes[0].attributes[0].vodmlid == new VodmlRef("ds:party.Organization.address")
        model.packages[0].objectTypes[0].attributes[1].dataType == [vodmlref: "ivoa:string"] as ElementRef
        model.packages[0].objectTypes[0].attributes[1].vodmlid == new VodmlRef("ds:party.Organization.phone")
        model.packages[0].objectTypes[0].attributes[2].dataType == [vodmlref: "ivoa:string"] as ElementRef
        model.packages[0].objectTypes[0].attributes[2].vodmlid == new VodmlRef("ds:party.Organization.email")
        model.packages[0].objectTypes[0].attributes[3].dataType == [vodmlref: "ivoa:anyURI"] as ElementRef
        model.packages[0].objectTypes[0].attributes[3].vodmlid == new VodmlRef("ds:party.Organization.logo")
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
        model.packages[0].vodmlid == new VodmlRef("ds:party")
        model.packages[0].objectTypes[0].name == "Organization"
        model.packages[0].objectTypes[0].vodmlid == new VodmlRef("ds:party.Organization")
        model.packages[0].objectTypes[0].vodmlid == new VodmlRef("ds:party.Organization")
        model.packages[0].objectTypes[0].extends_ == [vodmlref: "ds:party.Party"] as ElementRef
        model.packages[0].objectTypes[0].attributes[0].name == "address"
        model.packages[0].objectTypes[0].attributes[0].dataType == [vodmlref: "ivoa:string"] as ElementRef
        model.packages[0].objectTypes[0].attributes[0].vodmlid == new VodmlRef("ds:party.Organization.address")
        model.packages[0].objectTypes[0].attributes[1].dataType == [vodmlref: "ivoa:string"] as ElementRef
        model.packages[0].objectTypes[0].attributes[1].vodmlid == new VodmlRef("ds:party.Organization.phone")
        model.packages[0].objectTypes[0].attributes[2].dataType == [vodmlref: "ivoa:string"] as ElementRef
        model.packages[0].objectTypes[0].attributes[2].vodmlid == new VodmlRef("ds:party.Organization.email")
        model.packages[0].objectTypes[0].attributes[3].dataType == [vodmlref: "ivoa:anyURI"] as ElementRef
        model.packages[0].objectTypes[0].attributes[3].vodmlid == new VodmlRef("ds:party.Organization.logo")
    }
}
