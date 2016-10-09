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

import cfa.vo.vodml.instance.ModelImportInstance
import cfa.vo.vodml.instance.ObjectInstance
import cfa.vo.vodml.instance.DataModelInstance
import groovy.util.slurpersupport.GPathResult

class VotableReader extends AbstractXmlReader {
    private static cfa.vo.vodml.io.VodmlReader modelReader = new cfa.vo.vodml.io.VodmlReader()

    @Override
    DataModelInstance read(GPathResult xml) {
        def instance = new DataModelInstance()
        for (GPathResult modelXml in xml.GROUP) {
            instance << model(modelXml)
        }
        for (GPathResult objectXml in xml.RESOURCE.collect{ it.depthFirst().findAll{
            it.name() == "GROUP" &&
                    it.VODML.TYPE.text() &&
                    !it.VODML.ROLE.text() // Root instances
        }}.flatten()) {
            instance << object(objectXml)
        }
        return instance
    }

    private static String getParamWithRoleValue(xml, role) {
        return xml.PARAM.find{ it.VODML.ROLE.text() == "${VotableWriter.VODML_PREF}:$role" }.@value
    }

    private static ModelImportInstance model(xml) {
        def model = new ModelImportInstance()
        model.name = getParamWithRoleValue(xml, "Model.name")
        model.identifier = getParamWithRoleValue(xml, "Model.identifier")
        model.vodmlURL = getParamWithRoleValue(xml, "Model.url")
        model.documentationURL = getParamWithRoleValue(xml, "Model.documentationURL")
        return model
    }

    private static ObjectInstance object(xml) {
        def obj = new ObjectInstance()
        obj.type = xml.VODML.TYPE.text()
        def role = xml.VODML.ROLE.text()
        if (role) {
            obj.role = role
        }
        return obj
    }
}
