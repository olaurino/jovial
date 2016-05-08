package cfa.vo.vodml.io.factories.model

import cfa.vo.vodml.metamodel.ElementRef
import cfa.vo.vodml.metamodel.ObjectType

class ObjectTypeFactory extends AbstractVodmlFactory {

    @Override
    Object newInstance(String name, Map attributes) throws InstantiationException, IllegalAccessException {
        def parent = attributes.remove("parent")
        def objectType = attributes as ObjectType
        if (parent) {
            objectType.extends_ = [vodmlref: parent] as ElementRef
        }
        return objectType
    }
}
