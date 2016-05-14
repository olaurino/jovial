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

import cfa.vo.vodml.VodmlException
import cfa.vo.vodml.io.factories.model.*
import cfa.vo.vodml.metamodel.*
import cfa.vo.vodml.utils.VodmlRef

class ModelBuilder extends FactoryBuilderSupport {
    private static final String MODEL_PACKAGE = "cfa.vo.vodml.io.factories.model"

    public ModelBuilder() {
        def packageFactory = new BeanFactory(Package)
        def importFactory = new ModelImportFactory()
        def simpleStringFactory = new SimpleStringFactory()
        registerFactory("primitiveType", new TypeFactory(PrimitiveType))
        registerFactory("dataType", new TypeFactory(DataType))
        registerFactory("objectType", new TypeFactory(ObjectType))
        registerFactory("pack", packageFactory)
        registerFactory("package", packageFactory)
        registerFactory("include", importFactory)
        registerFactory("import", importFactory)
        registerFactory("lastModified", new DateTimeFactory())
        registerFactory("enumeration", new TypeFactory(Enumeration_))
        registerFactory("literal", new BeanFactory(EnumLiteral))
        registerFactory("subsets", new SubsettedRoleFactory())
        ["title", "description", "author"].each {
            registerFactory(it, simpleStringFactory)
        }
    }

    @Override
    protected Factory resolveFactory(name, Map attrs, value) {
        try {
            return Class.forName("$MODEL_PACKAGE.${name.capitalize()}Factory").newInstance()
        } catch (ClassNotFoundException | ClassCastException ex) {
            def ret = super.resolveFactory(name, attrs, value)
            if (ret == null) {
                throw new VodmlException("'$name' is not a valid node type", ex)
            } else {
                return ret
            }
        }
    }

    @Override
    protected void setParent(Object parent, Object child) {
        try {
            parent << child
        } catch (Exception ex) {
            throw new VodmlException("Cannot attach child $child to parent $parent. Does parent implement leftShift?", ex)
        }
    }

    @Override
    Object getVariable(String name) {
        try {
            super.getVariable(name)
        } catch (MissingPropertyException ignored) {
            new VodmlRef(name, "")
        }
    }
}
