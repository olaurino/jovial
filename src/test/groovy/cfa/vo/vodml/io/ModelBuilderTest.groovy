package cfa.vo.vodml.io

import ca.odell.glazedlists.EventList
import cfa.vo.vodml.VodmlException
import cfa.vo.vodml.utils.VodmlRef
import spock.lang.Specification

class ModelBuilderTest extends Specification {
    def builder = new ModelBuilder()

    def "test no factory"() {
        when:
        builder.model() {
            foo() {}
        }
        then:
        def ex = thrown(VodmlException)
        ex.message == "'foo' is not a valid node type"
    }

    def "test builder"() {
        when:
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
        then:
        model.name == "test"
        model.version == "1.0"
        model.description == "A Description"
        model.authors == ["Jane Doe", "John Doe"] as EventList
        model.packages[0].name == "aPackage"
        model.packages[0].dataTypes[0].name == "DataType"
        model.packages[0].dataTypes[0].attributes[0].name == "anAttribute"
        model.packages[0].dataTypes[1].name == "SomeData"
        model.packages[0].dataTypes[1].attributes[0].name == "attr"

        and:
        model.packages[0].vodmlid == new VodmlRef("test:aPackage")
        model.packages[0].dataTypes[0].vodmlid == new VodmlRef("test:aPackage.DataType")
    }
}
