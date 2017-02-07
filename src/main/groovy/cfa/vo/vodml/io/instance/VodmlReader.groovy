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
package cfa.vo.vodml.io.instance

import cfa.vo.vodml.instance.*
import cfa.vo.vodml.utils.Resolver
import groovy.util.slurpersupport.GPathResult

class VodmlReader extends AbstractXmlReader {
    @Override
    DataModelInstance read(GPathResult xml) {
        def instance = new DataModelInstance()
        for (GPathResult modelXml in xml.model) {
            instance << model(modelXml)
        }
        for (GPathResult objectXml in xml.object) {
            instance << object(objectXml)
        }
        return instance
    }

    private static model(GPathResult xml) {
        def model = new ModelImportInstance()
        model.name = xml.vodmlrefPrefix?.text()
        model.identifier = xml.identifier?.text()
        model.vodmlURL = xml.vodmlURL?.text()
        model.documentationURL = xml.documentationURL?.text()
        return model
    }

    private static object(GPathResult xml) {
        def object = new ObjectInstance()
        object.type = xml.@vodmlRef
        for (GPathResult attrXml in xml.attribute) {
            def child = attrXml.children()[0]
            def attrTag = child.name()
            if (attrTag == "primitiveValue") {
                object << attribute(attrXml)
            } else if (attrTag == "dataObject") {
                def data = dataObject(child)
                data.role = attrXml.@vodmlRef
                object << data
            }
        }
        for (GPathResult collectionXml in xml.collection) {
            object << collection(collectionXml)
        }
        return object
    }

    private static dataObject(GPathResult xml) {
        def object = new AttributeInstance()
        object.type = xml.@vodmlRef
        for (GPathResult attrXml in xml.attribute) {
            object << attribute(attrXml)
        }
        return object
    }

    private static attribute(GPathResult xml) {
        def attr = new LiteralInstance()
        attr.role = xml.@vodmlRef
        GPathResult valueNode = xml.children()[0]
        attr.value = valueNode.text()
        attr.type = Resolver.instance.resolveTypeOfRole(attr.role).vodmlref
        return attr
    }

    private static collection(GPathResult xml) {
        def collection = new CompositionInstance()
        collection.role = xml.@vodmlRef

        for (GPathResult instance in xml.object) {
            collection << object(instance)
        }

        return collection
    }
}
