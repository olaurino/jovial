package cfa.vo.vodml.metamodel


trait Parent {
    def propagateVodmlid(ReferableElement child) {
        if (child.vodmlid == null) {
            child.vodmlid = vodmlid.append(child.name)
        }
    }
}