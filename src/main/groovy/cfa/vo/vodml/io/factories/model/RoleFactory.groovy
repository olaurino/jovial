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
package cfa.vo.vodml.io.factories.model

import cfa.vo.vodml.VodmlException
import cfa.vo.vodml.metamodel.ElementRef
import cfa.vo.vodml.metamodel.Multiplicity
import cfa.vo.vodml.metamodel.Role
import cfa.vo.vodml.utils.VodmlRef

abstract class RoleFactory extends AbstractVodmlFactory {
    @Override
    Object newInstance(String name, Map attributes) throws InstantiationException, IllegalAccessException {
        String multiplicity = attributes.remove("multiplicity")
        if (multiplicity) {
            try {
                def tokens = multiplicity.split("\\.\\.")
                def min = Integer.valueOf(tokens[0])
                def max
                if (tokens[1] == "-1" || tokens[1] == "*") {
                    max = -1
                } else {
                    max = Integer.valueOf(tokens[1])
                }
                attributes.multiplicity = [minOccurs: min, maxOccurs: max] as Multiplicity
            } catch (Exception ex) {
                throw new VodmlException(error(), ex)
            }
        }
        String dataType = attributes.remove("dataType").toString()
        if (!dataType.contains(":")) {
            dataType = dataType.replaceFirst("\\.", ":")
        }
        def role = getGenericType().newInstance(attributes)
        role.dataType = new ElementRef(vodmlref: new VodmlRef(dataType))
        return role
    }

    abstract Class<? extends Role> getGenericType()

    private error = {
        """
Illegal multiplicity expression. Please use '<minOccurs>..<maxOccurs>', where:
  <minOccurs> is a non negative integer, and
  <maxOccurs> is a positive integer, or '-1', or '*' for unbound multiplicity.
"""
    }
}
