package cfa.vo.vodml.metamodel

import ca.odell.glazedlists.EventList
import groovy.beans.Bindable
import groovy.transform.EqualsAndHashCode

@Bindable
@EqualsAndHashCode
class Enumeration_ extends ValueType implements Buildable {
    List<EnumLiteral> literals = [] as EventList

    void leftShift(EnumLiteral child) {
        literals.add(child)
        propagateVodmlid(child)
    }

    private propagateVodmlid(ReferableElement child) {
        if (child.vodmlid == null) {
            child.vodmlid = this.vodmlid.append(child.name)
        }
    }

    @Override
    void build(GroovyObject builder) {
        def elem = {
            enumeration() {
                "vodml-id"(this.vodmlid)
                name(this.name)
                description(this.description)
                if (this.extends_) {
                    "extends"() {
                        out << this.extends_
                    }
                }
                this.literals.each {
                    out << it
                }
            }
        }
        elem.delegate = builder
        elem()
    }
}
