package cfa.vo.vodml.metamodel

import groovy.beans.Bindable
import groovy.transform.EqualsAndHashCode


@Bindable
@EqualsAndHashCode
class Package extends ReferableElement implements Buildable, PackageLike {

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
