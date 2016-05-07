package cfa.vo.vodml.io.factories.model

import ca.odell.glazedlists.EventList
import cfa.vo.vodml.metamodel.Model

class ModelFactory extends AbstractVodmlFactory {
    @Override
    Object newInstance(Map attributes) throws InstantiationException, IllegalAccessException {
        def authors = attributes.remove("authors") as EventList
        def model = attributes as Model
        model.authors = authors
        return model
    }
}
