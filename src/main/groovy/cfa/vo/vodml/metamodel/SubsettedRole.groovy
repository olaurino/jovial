package cfa.vo.vodml.metamodel

import groovy.beans.Bindable
import groovy.transform.EqualsAndHashCode


@Bindable
@EqualsAndHashCode
class SubsettedRole extends Constraint {
    ElementRef role
    ElementRef dataType
    SemanticConcept semanticConcept

    @Override
    void build(GroovyObject builder) {
        def elem = {
            constraint("xsi:type": "vo-dml:SubsettedRole") {
                if (this.description) {
                    "description"(this.description)
                }
                "role"() {
                    out << this.role
                }
                datatype {
                    out << this.dataType
                }
                if (this.semanticConcept) {
                    out << semanticConcept
                }
            }
        }
        elem.delegate = builder
        elem()
    }
}
