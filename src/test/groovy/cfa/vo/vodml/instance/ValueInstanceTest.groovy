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
package cfa.vo.vodml.instance

import cfa.vo.vodml.utils.TestResolver
import cfa.vo.vodml.utils.VodmlRef
import org.junit.Test


class ValueInstanceTest {
    // We need to instantiate a test resolver singleton so vodmlrefs are resolved.
    private TestResolver resolver = new TestResolver()

    @Test
    public void testString() {
        assert [datatype: "char", arraysize: '6'] == ColumnInstance.infer("string")
        assert [datatype: "char", arraysize: '5'] == ColumnInstance.infer("sting")
        assert [datatype: "int"] == ColumnInstance.infer(35)
        assert [datatype: "float"] == ColumnInstance.infer(3.5)
        assert [datatype: "float"] == ColumnInstance.infer(1.0)
        assert [datatype: "float", arraysize: '3'] == ColumnInstance.infer([1.0, 1.1, 1.2])
        assert [datatype: "int", arraysize: '2'] == ColumnInstance.infer([1, 2])
        assert [datatype: "float", arraysize: '3'] == ColumnInstance.infer([1.0, 1.1, 1.2].toArray())
        assert [datatype: "float", arraysize: '3'] == ColumnInstance.infer([1.0, 1.1, 1.2] as Set)
    }

    @Test
    public void testType() {
        ObjectInstance instance = new ObjectInstance(role: new VodmlRef("ds:party.Party.name"))
        assert new VodmlRef("ivoa:string") == instance.type
    }
}
