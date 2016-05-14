package cfa.vo.vodml.io.factories.model

import cfa.vo.vodml.metamodel.ModelImport


class ModelImportFactory extends AbstractVodmlFactory {
    @Override
    def newInstance(String name, Map attributes) {
        def url = attributes.url
        def docUrl = attributes.documentationURL
        if (url && !(url instanceof URI)) {
            attributes.url = new URI(url)
        }
        if (docUrl && !(docUrl instanceof URI)) {
            attributes.documentationURL = new URI(docUrl)
        }
        return attributes as ModelImport
    }
}
