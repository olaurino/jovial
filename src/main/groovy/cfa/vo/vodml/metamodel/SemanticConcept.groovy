package cfa.vo.vodml.metamodel

import groovy.beans.Bindable
import groovy.transform.EqualsAndHashCode


@Bindable
@EqualsAndHashCode
class SemanticConcept implements Buildable {
    String vocabularyURI
    String topConcept

    @Override
    void build(GroovyObject builder) {
        def elem = {
            if (topConcept || vocabularyURI) {
                semanticconcept() {
                    if (topConcept) {
                        topConcept(new URI(this.topConcept))
                    }
                    if (vocabularyURI) {
                        vocabularyURI(new URI(this.vocabularyURI))
                    }
                }
            }
        }
        elem.delegate = builder
        elem()
    }
}
