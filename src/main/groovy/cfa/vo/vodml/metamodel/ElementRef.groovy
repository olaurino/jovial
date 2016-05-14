package cfa.vo.vodml.metamodel

import cfa.vo.vodml.utils.VodmlRef
import groovy.beans.Bindable
import groovy.transform.EqualsAndHashCode


@Bindable
@EqualsAndHashCode
class ElementRef implements Buildable {
    VodmlRef vodmlref

    public setVodmlref(String ref) {
        vodmlref = new VodmlRef(ref)
    }

    @Override
    void build(GroovyObject builder) {
        def elem = {
            "vodml-ref"(this.vodmlref)
        }
        elem.delegate = builder
        elem()
    }
}
