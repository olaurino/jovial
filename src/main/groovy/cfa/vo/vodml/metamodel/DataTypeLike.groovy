package cfa.vo.vodml.metamodel

import ca.odell.glazedlists.BasicEventList


trait DataTypeLike extends Parent {
    def abstract_ = false
    List<Attribute> attributes = [] as BasicEventList
    List<Reference> references = [] as BasicEventList

    void leftShift(Attribute child) {
        attributes << child
        propagateVodmlid(child)
    }

    void leftShift(Reference child) {
        references << child
        propagateVodmlid(child)
    }
}