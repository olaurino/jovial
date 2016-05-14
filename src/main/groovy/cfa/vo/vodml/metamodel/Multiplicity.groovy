package cfa.vo.vodml.metamodel

import groovy.beans.Bindable
import groovy.transform.EqualsAndHashCode


@Bindable
@EqualsAndHashCode
class Multiplicity implements Buildable {
    Integer minOccurs = 1
    Integer maxOccurs = 1

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
