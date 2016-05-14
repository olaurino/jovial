package cfa.vo.vodml.io.factories.model

import cfa.vo.vodml.metamodel.Reference
import cfa.vo.vodml.metamodel.Role

class ReferenceFactory extends RoleFactory {
    @Override
    Class<? extends Role> getGenericType() {
        return Reference
    }
}
