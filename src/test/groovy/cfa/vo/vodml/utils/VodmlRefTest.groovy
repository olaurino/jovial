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

import org.junit.Assert
import org.junit.Test

class VodmlRefTest {
    @Test
    void testStringArg() {
        def ref = new VodmlRef("foo:bar")
        assert ref.prefix == "foo"
        assert ref.reference == "bar"
    }

    @Test
    void testStringArgNoPrefix() {
        def ref = new VodmlRef("bar")
        assert ref.prefix == null
        assert ref.reference == "bar"
    }

    @Test
    void testVodmlRefArg() {
        def orig = new VodmlRef("foo:bar")
        def ref = new VodmlRef(orig)
        assert ref.prefix == "foo"
        assert ref.reference == "bar"
    }

    @Test
    void testVodmlRefSetPrefixException() {
        try {
            def orig = new VodmlRef("foo:bar")
            new VodmlRef("spam", orig)
        } catch (IllegalStateException ex) {
            return // should fail
        }
        Assert.fail("Should have thrown exception")
    }

    @Test
    void testVodmlRefSetPrefix() {
        def orig = new VodmlRef("bar")
        def ref = new VodmlRef("spam", orig)
        assert ref.prefix == "spam"
        assert ref.reference == "bar"
    }

    @Test
    void testVodmlRefArgNoPrefix() {
        def orig = new VodmlRef("bar")
        def ref = new VodmlRef(orig)
        assert ref.prefix == null
        assert ref.reference == "bar"
    }

    @Test
    void testToString() {
        assert "foo:bar" == new VodmlRef("foo:bar").toString()
    }

    @Test
    void testToStringNoPrefix() {
        assert "bar" == new VodmlRef("bar").toString()
    }

    @Test
    void testGetProperty() {
        def ref = new VodmlRef("pre", "")
        assert ref.something == new VodmlRef("pre:something")

        // check calls are idempotent
        assert ref.something == ref.something
        assert "pre:something"  == ref.something.toString()

        // test chains
        assert ref.something.else == new VodmlRef("pre:something.else")

        // test usual properties still work
        assert ref.prefix == "pre"
        assert ref.reference == ""
        assert ref.something.prefix == "pre"
        assert ref.something.reference == "something"

    }
}
