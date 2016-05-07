package cfa.vo.vodml.io.factories.model

import cfa.vo.vodml.metamodel.Package

class PackageFactory extends AbstractVodmlFactory {
    @Override
    Object newInstance(Map attributes) throws InstantiationException, IllegalAccessException {
        return attributes as Package
    }
}
