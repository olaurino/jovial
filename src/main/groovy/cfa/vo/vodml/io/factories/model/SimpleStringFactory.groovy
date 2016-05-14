package cfa.vo.vodml.io.factories.model


class SimpleStringFactory extends AbstractVodmlFactory {
    @Override
    def newInstance(String name, Map attributes) {
        return [name: name, value: attributes.name] as StringAttribute
    }
}
