package cfa.vo.vodml.utils

import cfa.vo.vodml.metamodel.Model
import cfa.vo.vodml.metamodel.Role
import cfa.vo.vodml.metamodel.Type

@Singleton
class Resolver {
    private List<Model> models = []
    private Map<VodmlRef, Type> types = [:]
    private Map<VodmlRef, Role> roles = [:]

    public Type resolveType(String ref) {
        def key = new VodmlRef(ref)
        return types[key]
    }

    public Role resolveRole(String ref) {
        def key = new VodmlRef(ref)
        resolveRole(key)
    }

    public Role resolveRole(VodmlRef ref) {
        roles[ref]
    }

    public VodmlRef resolveAttribute(VodmlRef typeRef, String attributeName) {
        if (roles[attributeName]) {
            return roles[attributeName]
        }
        Type type = types[typeRef]
        def matches = match(type, attributeName)
        if (matches.size() == 1) {
            return new VodmlRef(typeRef.prefix, matches[0].vodmlid)
        }
        else if (matches.size() == 0) {
            throw new IllegalArgumentException(String.format("No Such Attribute '%s' in %s", attributeName, typeRef))
        } else if (matches.size() >1) {
            throw new IllegalArgumentException(String.format("Ambiguous Attribute '%s' in %s", attributeName, typeRef))
        }
    }

    public VodmlRef resolveAttribute(String typeref, String attributeName) {
        VodmlRef typeRef = new VodmlRef(typeref)
        resolveAttribute(typeRef, attributeName)
    }

    public leftShift(Model spec) {
        models << spec
        index(spec)
    }

    public boolean "extends"(String child, String parent) {
        this.extends(new VodmlRef(child), new VodmlRef(parent))
    }

    public boolean "extends"(VodmlRef child, VodmlRef parent) {
        Type childType = types[child]

        def typeExtends = childType.extends_

        if (typeExtends) {
            def directParent = typeExtends.vodmlref
            if (directParent == parent) {
                true
            } else {
                return "extends"(directParent, parent)
            }
        }
    }

    private match(Type type, String attributeName) {
        def matches = ["attributes", "references", "collections"].findResults {
            if (type.hasProperty(it)) {
                type."$it".findResults {
                    if (it.name == attributeName) {
                        it
                    }
                }
            }
        }.flatten()

        if (type.extends_) {
            type = types[new VodmlRef(type.extends_.vodmlref)]
            matches += match(type, attributeName)
        }
        matches
    }

    private void index(Model spec) {
        indexPackage(spec.name, spec)
        spec.packages.each {
            indexPackage(spec.name, it)
        }
    }

    private indexPackage(String prefix, pkg) {
        ["dataTypes", "objectTypes", "primitiveTypes", "enumerations"].each {
            pkg."$it".each { type ->
                VodmlRef key = new VodmlRef(prefix, type.vodmlid)
                types[key] = type

                ["attributes", "references", "collections"].each {
                    if (type.hasProperty(it)) {
                        type?."$it".each { role ->
                            VodmlRef rkey = new VodmlRef(prefix, role.vodmlid)
                            roles[rkey] = role
                        }
                    }
                }
            }
        }
    }
}

