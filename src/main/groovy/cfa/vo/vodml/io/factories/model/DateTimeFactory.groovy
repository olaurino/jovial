package cfa.vo.vodml.io.factories.model

import org.joda.time.DateTime


class DateTimeFactory extends AbstractVodmlFactory {
    @Override
    def newInstance(String name, Map attributes) {
        return DateTime.parse(attributes.name)
    }
}
