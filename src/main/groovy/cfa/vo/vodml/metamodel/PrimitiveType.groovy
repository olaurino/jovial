package cfa.vo.vodml.metamodel

import groovy.beans.Bindable
import groovy.transform.EqualsAndHashCode


@Bindable
@EqualsAndHashCode
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
