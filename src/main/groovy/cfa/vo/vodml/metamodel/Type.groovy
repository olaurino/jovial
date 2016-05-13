package cfa.vo.vodml.metamodel

import ca.odell.glazedlists.BasicEventList
import groovy.beans.Bindable
import groovy.transform.EqualsAndHashCode


@Bindable
@EqualsAndHashCode
abstract class Type extends ReferableElement {
    ElementRef extends_
    List<Constraint> constraints = [] as BasicEventList

    void leftShift(Constraint constraint) {
        constraints.add(constraint)
    }
}
