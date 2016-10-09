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
package cfa.vo.vodml.io;

import cfa.vo.vodml.metamodel.Model;
import cfa.vo.vodml.utils.XmlUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

public class VodmlWriterTest {
    private Model model;
    private ModelWriter writer;
    private String expected;

    @Before
    public void setUp() throws Exception {
        model = new Model();
        model.setName("something");
        model.setTitle("Some Title");
        model.setDescription("Some Description");
        model.setVersion("1.0-SNAPSHOT");
        model.setLastModified(new DateTime("2016-04-16T10:16:50.000Z").withZone(DateTimeZone.UTC));
        model.getAuthors().add("John Doe");

        writer = new ModelWriter();
        expected = makeString();
    }

    @Test
    public void testWrite() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        writer.write(model, os);
        String out = os.toString("UTF-8");
        XmlUtils.testXml(expected, out);
    }

    private String makeString() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "  <vo-dml:model xmlns:vo-dml=\"http://www.ivoa.net/xml/VODML/v1.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.ivoa.net/xml/VODML/v1.0 http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/xsd/vo-dml-v1.0.xsd\">\n" +
                "    <name>something</name>\n" +
                "    <description>Some Description</description>\n" +
                "    <title>Some Title</title>\n" +
                "    <author>John Doe</author>\n" +
                "    <version>1.0-SNAPSHOT</version>\n" +
                "    <lastModified>2016-04-16T10:16:50.000Z</lastModified>\n" +
                "  </vo-dml:model>";
    }
}
