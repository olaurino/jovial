package cfa.vo.vodml.io.factories.model

import ca.odell.glazedlists.EventList
import cfa.vo.vodml.metamodel.Model

class ModelFactory extends AbstractFactory {
    @Override
    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        def authors = attributes.remove("authors") as EventList
        def model = attributes as Model
        model.authors = authors
        return model
    }
}
