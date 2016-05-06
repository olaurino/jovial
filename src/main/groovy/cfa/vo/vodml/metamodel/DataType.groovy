package cfa.vo.vodml.metamodel

import groovy.beans.Bindable
import groovy.transform.EqualsAndHashCode


@Bindable
@EqualsAndHashCode
class DataType extends ValueType implements Buildable {
    def abstract_ = false
    List<Attribute> attributes
    List<Reference> references

    @Override
    void build(GroovyObject builder) {
        def args = []
        if (abstract_) {
            args = ["abstract": true]
        }
        def elem = {
            dataType(args) {
                "vodml-id"(this.vodmlid)
                name(this.name)
                description(this.description)
                if (extends_) {
                    "extends"(this.extends_)
                }
                this.constraints.each {
                    out << it
                }
                attributes.each {
                    out << it
                }
                references.each {
                    out << it
                }
            }
        }
        elem.delegate = builder
        elem()
    }
}
