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

import org.junit.Test
// TODO docs and error handling
class ResolverTest {
    private TestResolver resolver = new TestResolver()

    @Test
    public void testIndex() {
        assert resolver.resolveType("ds:dataset.Dataset").name == "Dataset"
        assert resolver.resolveType("ds:party.Organization").name == "Organization"
        assert resolver.resolveRole("ds:party.Party.name").name == "name"

        assert resolver.extends("ds:party.Individual", "ds:party.Party")
        assert !resolver.extends("ds:party.Individual", "ds:dataset.Dataset")
        assert resolver.extends("ds:dataset.Publisher", "ds:party.Role")

        assert resolver.resolveAttribute("ds:party.Party", "name") == new VodmlRef("ds:party.Party.name")
        assert resolver.resolveAttribute("ds:party.Organization", "name") == new VodmlRef("ds:party.Party.name")
    }

    @Test
    public void resolveTypeOfRole() {
        def roleRef = new VodmlRef("ds:party.Party.name")
        assert resolver.resolveTypeOfRole(roleRef).vodmlref == new VodmlRef("ivoa:string")
    }
}
