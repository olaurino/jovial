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
package cfa.vo.vodml.io

import ca.odell.glazedlists.BasicEventList
import cfa.vo.vodml.metamodel.*
import groovy.util.slurpersupport.GPathResult
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

class VodmlReader {
    private slurper = new XmlSlurper()

    Model read(InputStream is) {
        def parsed = slurper.parse(is)
        return modelFrom(parsed)
    }

    Model read(File file) {
        read(new FileInputStream(file))
    }

    private Model modelFrom(GPathResult xml) {
        new Model(
                name: xml.name,
                description: xml.description,
                title: xml.title,
                version: xml.version,
                lastModified: new DateTime(xml.lastModified.toString()).withZone(DateTimeZone.UTC),
                authors: xml.author?.collect {
                    it.text()
                } as BasicEventList,
                previousVersions: xml.previousVersions?.collect {
                    it.text()
                } as BasicEventList,
                imports: xml.import?.collect {
                    importFrom(it)
                } as BasicEventList,
                packages: xml.package?.collect {
                    packageFrom(it)
                } as BasicEventList,
                objectTypes: xml.objectType?.collect {
                    objectTypeFrom(it)
                } as BasicEventList,
                dataTypes: xml.dataType?.collect {
                    dataTypeFrom(it)
                } as BasicEventList,
                enumerations: xml.enumeration?.collect {
                    enumerationFrom(it)
                } as BasicEventList,
                primitiveTypes: xml.primitiveType.collect {
                    primitiveTypeFrom(it)
                } as BasicEventList
        )
    }

    private ModelImport importFrom(GPathResult xml) {
        new ModelImport(
                name: xml.name,
                version: xml.version,
                url: new URI(xml.url.text()),
                documentationURL: new URI(xml.documentationURL.text())
        )
    }

    private Package packageFrom(GPathResult xml) {
        new Package(
                name: xml.name,
                vodmlid: xml."vodml-id",
                description: xml.description,
                objectTypes: xml.objectType?.collect {
                    objectTypeFrom(it)
                } as BasicEventList,
                dataTypes: xml.dataType?.collect {
                    dataTypeFrom(it)
                } as BasicEventList,
                enumerations: xml.enumeration?.collect {
                    enumerationFrom(it)
                } as BasicEventList,
                primitiveTypes: xml.primitiveType.collect {
                    primitiveTypeFrom(it)
                } as BasicEventList,
                packages: xml.package?.collect {
                    packageFrom(it)
                } as BasicEventList,
        )
    }

    private ObjectType objectTypeFrom(GPathResult xml) {
        new ObjectType(
                abstract_ : xml.@abstract.text(),
                name: xml.name,
                vodmlid: xml."vodml-id",
                description: xml.description,
                extends_: elementRefFrom(xml.extends),
                attributes: xml.attribute.collect {
                    attributeFrom(it)
                } as BasicEventList,
                collections: xml.collection.collect {
                    compositionFrom(it)
                } as BasicEventList,
                references: xml.reference.collect {
                    referenceFrom(it)
                } as BasicEventList,
                constraints: xml.constraint.collect {
                    constraintFrom(it)
                } as BasicEventList,
        )
    }

    private DataType dataTypeFrom(GPathResult xml) {
        new DataType(
                abstract_ : xml.@abstract.text(),
                name: xml.name,
                vodmlid: xml."vodml-id",
                description: xml.description,
                extends_: elementRefFrom(xml.extends),
                attributes: xml.attribute?.collect {
                    attributeFrom(it)
                } as BasicEventList,
                references: xml.reference.collect {
                    referenceFrom(it)
                } as BasicEventList,
                constraints: xml.constraint.collect {
                    constraintFrom(it)
                } as BasicEventList
        )
    }

    private Enumeration_ enumerationFrom(GPathResult xml) {
        new Enumeration_(
                name: xml.name,
                vodmlid: xml."vodml-id",
                description: xml.description,
                extends_: elementRefFrom(xml.extends),
                literals: xml.literal?.collect {
                    new EnumLiteral(
                            name: it.name,
                            vodmlid: it."vodml-id",
                            description: it.description,
                    )
                } as BasicEventList
        )
    }

    private PrimitiveType primitiveTypeFrom(GPathResult xml) {
        new PrimitiveType(
                name: xml.name,
                vodmlid: xml."vodml-id",
                description: xml.description,
                extends_: elementRefFrom(xml.extends),
        )
    }

    private ElementRef elementRefFrom(GPathResult xml) {
        if(xml.size()) {
            new ElementRef(
                    vodmlref: xml."vodml-ref"
            )
        }
    }

    private SemanticConcept semanticConceptFrom(GPathResult xml) {
        new SemanticConcept(
                topConcept: xml.topConcept.text(),
                vocabularyURI: xml.vocabularyURI.text()
        )
    }

    private Attribute attributeFrom(GPathResult xml) {
        new Attribute(
                name: xml.name,
                vodmlid: xml."vodml-id",
                description: xml.description,
                dataType: elementRefFrom(xml.datatype),
                semanticConcepts: xml.semanticconcept?.collect {
                    semanticConceptFrom(it)
                },
                multiplicity: multiplicityFrom(xml.multiplicity)
        )
    }

    private Composition compositionFrom(GPathResult xml) {
        new Composition(
                name: xml.name,
                vodmlid: xml."vodml-id",
                description: xml.description,
                dataType: elementRefFrom(xml.datatype),
                multiplicity: multiplicityFrom(xml.multiplicity)
        )
    }

    private Multiplicity multiplicityFrom(GPathResult xml) {
        new Multiplicity(
                minOccurs: Integer.parseInt(xml.minOccurs.text()),
                maxOccurs: Integer.parseInt(xml.maxOccurs.text())
        )
    }

    private Reference referenceFrom(GPathResult xml) {
        new Reference(
            name: xml.name,
            vodmlid: xml."vodml-id",
            description: xml.description,
            dataType: elementRefFrom(xml.datatype),
            multiplicity: multiplicityFrom(xml.multiplicity)
        )
    }

    private Constraint constraintFrom(GPathResult xml) {
        if (xml."@xsi:type"?.equals("vo-dml:SubsettedRole")) {
            new SubsettedRole(
                    role: elementRefFrom(xml.role),
                    dataType: elementRefFrom(xml.datatype),
                    semanticConcept: semanticConceptFrom(xml.semanticconcept)
            )
        } else {
            new Constraint(
                    description: xml.description
            )
        }
    }
}