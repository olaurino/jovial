package cfa.vo.vodml.io.factories.model

import cfa.vo.vodml.metamodel.ElementRef
import cfa.vo.vodml.metamodel.ReferableElement

class TypeFactory extends BeanFactory {

    public TypeFactory(Class<? extends ReferableElement> clazz) {
        super(clazz)
    }

    @Override
    Object newInstance(String name, Map attributes) throws InstantiationException, IllegalAccessException {
        def parent = attributes.remove("parent")
        if (!parent) {
            parent = attributes.remove("extends")
        }
        def type = super.newInstance(name, attributes)
        if (parent) {
            type.extends_ = [vodmlref: parent] as ElementRef
        }
        return type
    }
}
