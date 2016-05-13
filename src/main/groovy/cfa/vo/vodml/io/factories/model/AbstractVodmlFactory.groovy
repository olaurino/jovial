package cfa.vo.vodml.io.factories.model

abstract class AbstractVodmlFactory extends AbstractFactory {
    protected String defaultAttributeName = "name"

    @Override
    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        attributes = fixName(value, attributes)
        return newInstance(name, attributes)
    }

    abstract newInstance(String name, Map attributes)

    protected fixName(value, Map attributes) {
        if (!(defaultAttributeName in attributes) && value) {
            try {
                attributes[defaultAttributeName] = value
            } catch (UnsupportedOperationException ignored) {
                attributes = [:]
                attributes[defaultAttributeName] = value
            }
        }
        return attributes
    }

}
