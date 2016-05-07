package cfa.vo.vodml.io.factories.model

import cfa.vo.vodml.metamodel.Attribute
import cfa.vo.vodml.metamodel.Role

class AttributeFactory extends RoleFactory {
    @Override
    Class<? extends Role> getGenericType() {
        return Attribute
    }
}
