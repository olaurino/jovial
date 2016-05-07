package cfa.vo.vodml.metamodel

import groovy.beans.Bindable
import groovy.transform.EqualsAndHashCode


@Bindable
@EqualsAndHashCode
class Package extends ReferableElement implements Buildable {
    List<PrimitiveType> primitiveTypes = []
    List<Enumeration_> enumerations = []
    List<DataType> dataTypes = []
    List<ObjectType> objectTypes = []
    List<Package> packages = []

    void leftShift(DataType dataType) {
        dataTypes << dataType
        if (dataType.vodmlid == null) {
            dataType.vodmlid = this.vodmlid.append(dataType.name)
        }
    }

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
