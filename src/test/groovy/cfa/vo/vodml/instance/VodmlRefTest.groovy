package cfa.vo.vodml.instance

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
    void testVodmlRefArgNoPrefix() {
        def orig = new VodmlRef("bar")
        def ref = new VodmlRef(orig)
        assert ref.prefix == null
        assert ref.reference == "bar"
    }
}
