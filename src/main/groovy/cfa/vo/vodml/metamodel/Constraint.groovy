package cfa.vo.vodml.metamodel

import groovy.beans.Bindable
import groovy.transform.EqualsAndHashCode


@Bindable
@EqualsAndHashCode
class Constraint implements Buildable {
    String description

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
