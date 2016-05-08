package cfa.vo.vodml.io.factories.model

import cfa.vo.vodml.metamodel.ModelImport


class ModelImportFactory extends AbstractVodmlFactory {
    @Override
    def newInstance(String name, Map attributes) {
        def url = attributes.url
        def docUrl = attributes.documentationURL
        if (url && !(url instanceof URL)) {
            attributes.url = new URL(url)
        }
        if (docUrl && !(docUrl instanceof URL)) {
            attributes.documentationURL = new URL(docUrl)
        }
        return attributes as ModelImport
    }
}
