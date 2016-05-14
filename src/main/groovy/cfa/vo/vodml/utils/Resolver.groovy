/*
 * #%L
 * jovial
 * %%
 * Copyright (C) 2016 Smithsonian Astrophysical Observatory
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Smithsonian Astrophysical Observatory nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
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
                VodmlRef key
                if (!type.vodmlid || !type.vodmlid.prefix) {
                    key = new VodmlRef(prefix, type.vodmlid)
                } else {
                    key = type.vodmlid
                }
                types[key] = type

                ["attributes", "references", "collections"].each {
                    if (type.hasProperty(it)) {
                        type?."$it".each { role ->
                            VodmlRef rkey = role.vodmlid
                            if (!role.vodmlid.prefix) {
                                rkey = new VodmlRef(prefix, role.vodmlid)
                            }
                            roles[rkey] = role
                        }
                    }
                }
            }
        }
    }
}

