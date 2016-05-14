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

import groovy.transform.Canonical


@Canonical
class VodmlRef {
    String prefix
    String reference

    public VodmlRef(String prefix, String reference) {
        this.prefix = prefix
        this.reference = reference
    }

    public VodmlRef(String ref) {
        List tokens = ref.split(":") as List // Lists return null if index is out of bounds
        prefix = tokens[1] ? tokens[0] : null // No ':' means no prefix
        reference = tokens[1] ?: tokens[0]
    }

    public VodmlRef(String prefix, VodmlRef ref) {
        if (ref.prefix) {
            throw new IllegalStateException("Setting prefix on vodmlref with prefix")
        }
        this.prefix = prefix
        this.reference = ref.reference
    }

    public VodmlRef(VodmlRef ref) {
        this.prefix = ref.prefix
        this.reference = ref.reference
    }

    public VodmlRef append(String newPart) {
        return new VodmlRef(prefix, "$reference.$newPart")
    }

    def getProperty(String name) {
        if (name in this.getProperties().collect{
            it-> it.key
        }) {
            return this.@"$name"
        }
        def ref
        if (!reference) {
            ref = name
        } else {
            ref = "$reference.$name"
        }
        return new VodmlRef(prefix, ref)
    }

    @Override
    String toString() {
        def start = prefix ? "$prefix:" : ""
        return "$start$reference"
    }
}
