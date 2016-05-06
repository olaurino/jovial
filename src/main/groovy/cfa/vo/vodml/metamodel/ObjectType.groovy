package cfa.vo.vodml.metamodel

import groovy.beans.Bindable
import groovy.transform.EqualsAndHashCode


@Bindable
@EqualsAndHashCode
class ObjectType extends Type implements Buildable {
    def abstract_ = false
    List<Attribute> attributes
    List<Composition> collections
    List<Reference> references

    @Override
    void build(GroovyObject builder) {
        def elem = {
            def args = []
            if (abstract_) {
                args = ["abstract": true]
            }
            objectType(args) {
                "vodml-id"(this.vodmlid)
                name(this.name)
                description(this.description)
                if (this.extends_) {
                    "extends"() {
                        out << this.extends_
                    }
                }
                this.constraints.each {
                    out << it
                }
                this.attributes.each {
                    out << it
                }
                this.collections.each {
                    out << it
                }
                this.references.each {
                    out << it
                }
            }
        }
        elem.delegate = builder
        elem()
    }
}
