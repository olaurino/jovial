package cfa.vo.vodml.io.factories.model

import cfa.vo.vodml.metamodel.Constraint

class ConstraintFactory extends AbstractVodmlFactory {
    ConstraintFactory() {
        defaultAttributeName = "description"
    }

    @Override
    def newInstance(String name, Map attributes) {
        return attributes as Constraint
    }
}
