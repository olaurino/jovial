/*
 * #%L
 * jovial
 * %%
 * Copyright (C) 2016 - 2018 Smithsonian Astrophysical Observatory
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
package cfa.vo.vodml.io

import cfa.vo.vodml.metamodel.ElementRef
import cfa.vo.vodml.metamodel.Model
import cfa.vo.vodml.metamodel.Role
import cfa.vo.vodml.utils.Resolver

/**
 * FIXME We should do a proper graph resolution to make sure dependencies (supertypes) are serialized first,
 * but for now we are relying on the order to be meaningful, so we assume supertypes are defined before
 * subtypes.
 */
class PythonModelWriter {
    Resolver resolver = Resolver.instance
    VodmlReader reader = new VodmlReader()
    def namespace

    def write(Model model, OutputStream os) {
        namespace = model.name
        resolver << model
        model.imports.each{resolver << reader.read(it.url.toURL().openStream())}
        def stringBuilder = "" << build_package(model)
        os.print(stringBuilder.toString())
    }

    def build_package(modelOrPackage) {
        def stringBuilder = StringBuilder.newInstance()

        modelOrPackage.enumerations.each { enumeration  ->
            stringBuilder << buildEnumeration(enumeration)
        }

        ['dataTypes', 'objectTypes'].each {
            modelOrPackage."$it".each { type ->
                stringBuilder << build(type)
            }
        }

        modelOrPackage.packages.each { pkg ->
            stringBuilder << build_package(pkg)
        }

        return stringBuilder.toString()
    }

    def buildEnumeration(enumeration) {
        def stringBuilder = StringBuilder.newInstance()

        stringBuilder << """

@VO('$namespace:$enumeration.vodmlid')
class $enumeration.name(StringQuantity):
    pass\n"""

        return stringBuilder.toString()
    }

    def build(objectOrDataType) {
        def parentName = ""
        ElementRef parentRef = objectOrDataType?.extends_
        if (parentRef) {
            parentName = resolver.resolveType(parentRef.vodmlref).name
        }
        def parentString = parentName ? "($parentName)" : ""

        def stringBuilder = StringBuilder.newInstance()

        stringBuilder << """

@VO('$namespace:$objectOrDataType.vodmlid')
class $objectOrDataType.name$parentString:\n"""

        def empty = true
        ['attributes', 'compositions', 'references'].each { propertyName ->
            if (objectOrDataType.hasProperty(propertyName)) {
                def roleName = propertyName.capitalize()[0..-2]
                objectOrDataType."$propertyName".each { Role property ->
                    empty = false
                    def name = toSnakeCase(property.name)
                    def vodmlid = "$namespace:$property.vodmlid"
                    def multi =
                            ", min_occurs=$property.multiplicity.minOccurs, max_occurs=$property.multiplicity.maxOccurs"
                    stringBuilder << "    $name = $roleName('$vodmlid'$multi)\n"
                }
            }
        }

        if (empty) {
            stringBuilder << """    pass\n"""
        }

        return stringBuilder.toString()
    }

    private static String toSnakeCase(String text) {
        text.replaceAll(/([A-Z])/,/_$1/ ).toLowerCase().replaceAll(/^_/,'')
    }
}
