package cfa.vo.vodml.metamodel

import groovy.beans.Bindable
import groovy.transform.EqualsAndHashCode


@Bindable
@EqualsAndHashCode
abstract class Type extends ReferableElement {
    ElementRef extends_
    List<Constraint> constraints
}
