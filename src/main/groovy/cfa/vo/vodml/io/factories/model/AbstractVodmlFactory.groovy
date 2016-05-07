package cfa.vo.vodml.io.factories.model

abstract class AbstractVodmlFactory extends AbstractFactory {
    @Override
    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        attributes = fixName(value, attributes)
        return newInstance(attributes)
    }

    protected fixName(value, Map attributes) {
        if (!("name" in attributes) && value) {
            try {
                attributes.name = value
            } catch (UnsupportedOperationException ignored) {
                attributes = [:]
                attributes.name = value
            }
        }
        return attributes
    }

    abstract newInstance(Map attributes)

}
