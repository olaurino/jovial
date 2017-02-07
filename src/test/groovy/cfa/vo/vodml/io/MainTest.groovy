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
package cfa.vo.vodml.io

import cfa.vo.vodml.utils.XmlUtils
import spock.lang.Specification


class MainTest extends Specification {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream()
    private final PrintStream stdout = System.out

    def setup() {
        System.setOut(new PrintStream(outContent))
    }

    def cleanup() {
        System.setOut(stdout)
    }

    def "test main"() {
        given: "test arguments"
        def tempFile = File.createTempFile("test", "dsl")
        tempFile.write("model('testModel'){}")
        String[] args = [tempFile.absolutePath,]
        when: "main is called"
        Main.main(args)
        then: "a model is printed to standard output"
        XmlUtils.testXml(basicModel, outContent.toString())
    }

    def basicModel = """<?xml version="1.0" encoding="UTF-8"?>
<vo-dml:model xmlns:vo-dml="http://www.ivoa.net/xml/VODML/v1.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://www.ivoa.net/xml/VODML/v1.0 http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/xsd/vo-dml-v1.0.xsd">
<name>testModel</name>
<description/>
<title>My Model</title>
<version>1.0</version>
<lastModified>2016-10-27T16:44:47.097-04:00</lastModified>
</vo-dml:model>
"""
}
