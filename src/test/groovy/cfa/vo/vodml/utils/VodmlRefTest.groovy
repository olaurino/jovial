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
