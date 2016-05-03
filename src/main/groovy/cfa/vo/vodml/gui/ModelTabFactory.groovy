package cfa.vo.vodml.gui


class ModelTabFactory extends AbstractFactory {
    @Override
    Object newInstance(FactoryBuilderSupport fbs, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        if (value == null) {
            throw new InstantiationException("Please provide a PresentationModel value, none was provided")
        }
        if (attributes) {
            throw new InstantiationException("No attributes allowed in building element. Only a value accepted")
        }
        return new ModelTab(value)
    }
}
