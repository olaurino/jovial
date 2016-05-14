package cfa.vo.vodml.metamodel

import groovy.beans.Bindable
import groovy.transform.EqualsAndHashCode


@Bindable
@EqualsAndHashCode
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
