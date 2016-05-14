package cfa.vo.vodml.io.factories.model

import ca.odell.glazedlists.EventList
import cfa.vo.vodml.metamodel.Model
import org.joda.time.DateTime

class ModelFactory extends AbstractVodmlFactory {
    @Override
    Object newInstance(String name, Map attributes) throws InstantiationException, IllegalAccessException {
        def authors = attributes.remove("authors") as EventList
        def lastModified = attributes.remove("lastModified")
        if (authors) {
            attributes.authors = authors
        }
        if (lastModified) {
            attributes.lastModified = DateTime.parse(lastModified)
        }
        return attributes as Model
    }
}
