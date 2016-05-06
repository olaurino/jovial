package cfa.vo.vodml.metamodel

import groovy.beans.Bindable
import groovy.transform.EqualsAndHashCode


@Bindable
@EqualsAndHashCode
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
