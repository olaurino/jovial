package cfa.vo.vodml.metamodel

import groovy.beans.Bindable
import groovy.transform.EqualsAndHashCode


@Bindable
@EqualsAndHashCode
abstract class Role extends ReferableElement {
    ElementRef dataType
    Multiplicity multiplicity = new Multiplicity()
}
