package cfa.vo.vodml.instance

import org.junit.Test


class ValueInstanceTest {
    @Test
    public void testString() {
        assert [datatype: "char", arraysize: '6'] == ValueInstance.infer("string")
        assert [datatype: "char", arraysize: '5'] == ValueInstance.infer("sting")
        assert [datatype: "int", arraysize: '1'] == ValueInstance.infer(35)
        assert [datatype: "float", arraysize: '1'] == ValueInstance.infer(3.5)
        assert [datatype: "float", arraysize: '1'] == ValueInstance.infer(1.0)
        assert [datatype: "float", arraysize: '3'] == ValueInstance.infer([1.0, 1.1, 1.2])
        assert [datatype: "int", arraysize: '2'] == ValueInstance.infer([1, 2])
        assert [datatype: "float", arraysize: '3'] == ValueInstance.infer([1.0, 1.1, 1.2].toArray())
        assert [datatype: "float", arraysize: '3'] == ValueInstance.infer([1.0, 1.1, 1.2] as Set)
    }
}
