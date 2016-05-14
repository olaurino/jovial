package cfa.vo.vodml.io.factories.model

import cfa.vo.vodml.metamodel.Composition
import cfa.vo.vodml.metamodel.Role


class CollectionFactory extends RoleFactory {
    @Override
    Class<? extends Role> getGenericType() {
        return Composition
    }
}
