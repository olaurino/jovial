package cfa.vo.vodml.metamodel

import ca.odell.glazedlists.BasicEventList

trait PackageLike extends Parent {
    List<PrimitiveType> primitiveTypes = [] as BasicEventList
    List<Enumeration_> enumerations = [] as BasicEventList
    List<DataType> dataTypes = [] as BasicEventList
    List<ObjectType> objectTypes = [] as BasicEventList
    List<Package> packages = [] as BasicEventList

    void leftShift(PrimitiveType child) {
        primitiveTypes.add(child)
        propagateVodmlid(child)
    }

    void leftShift(Enumeration_ child) {
        enumerations.add(child)
        propagateVodmlid(child)
    }

    void leftShift(DataType child) {
        dataTypes.add(child)
        propagateVodmlid(child)
    }

    void leftShift(ObjectType child) {
        objectTypes.add(child)
        propagateVodmlid(child)
    }

    void leftShift(Package child) {
        packages << child
        propagateVodmlid(child)
    }
}