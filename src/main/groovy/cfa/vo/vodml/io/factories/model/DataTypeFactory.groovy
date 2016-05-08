package cfa.vo.vodml.io.factories.model

import cfa.vo.vodml.metamodel.DataType
import cfa.vo.vodml.metamodel.ElementRef


class DataTypeFactory extends AbstractVodmlFactory {
    @Override
    Object newInstance(String name, Map attributes) throws InstantiationException, IllegalAccessException {
        def parent = attributes.remove("parent")
        def dataType = attributes as DataType
        if (parent) {
            dataType.extends_ = [vodmlref: parent] as ElementRef
        }
        return dataType
    }
}
