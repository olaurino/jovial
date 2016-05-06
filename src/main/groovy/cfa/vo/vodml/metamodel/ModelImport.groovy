package cfa.vo.vodml.metamodel

import groovy.beans.Bindable
import groovy.transform.EqualsAndHashCode

@Bindable
@EqualsAndHashCode
class ModelImport implements Buildable {
    String name
    String version
    URL url
    URL documentationURL

    @Override
    void build(GroovyObject builder) {
        def modimp = {
            "import"() {
                name(this.name)
                version(this.version)
                url(this.url)
                documentationURL(this.documentationURL)
            }
        }
        modimp.delegate = builder
        modimp()
    }
}
