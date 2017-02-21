package cfa.vo.vodml.utils;

import org.junit.Test;

public class XmlUtilsTest {
    private String baseline = "<TABLE>" +
            "<FIELD ID='a' datatype='b' name='c'/>" +
            "<FIELD ID='b' datatype='char' arraysize='*'/>" +
            "</TABLE>";
    private String test = "<TABLE>" +
            "<FIELD ID='a' datatype='b'/>" +
            "<FIELD ID='b' datatype='char' arraysize='3' name='d'/>" +
            "</TABLE>";

    @Test
    public void testField() {
        XmlUtils.testVotable(baseline, test);
    }

}