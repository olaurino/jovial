package cfa.vo.vodml.metamodel

import cfa.vo.vodml.utils.VodmlRef
import groovy.beans.Bindable
import groovy.transform.EqualsAndHashCode


@Bindable
@EqualsAndHashCode
abstract class ReferableElement {
    String name
    VodmlRef vodmlid
    String description

    public setVodmlid(String ref) {
        vodmlid = new VodmlRef(ref)
    }

    @Override
    public String toString() {
        return name
    }
}
