package cfa.vo.vodml.io

import org.xml.sax.SAXParseException

import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory

/**
 * Simple boolean validator for vodml model definition documents.
 * The validator simply returns true if the document is valid.
 * Otherwise it throws an exception. Future versions will be more refined
 */
class Validator {
    private URL schema = getClass().getResource("/vodml-v1.0.xsd");

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


    /**
     * validate resource by URL
     * @param file a URL pointing to the resource
     * @return true if the document validated
     * @throws SAXParseException if the document is invalid
     */
    boolean validate(URL file) throws SAXParseException {
        file.withInputStream { xml ->
            validate(xml)
        }
    }

    /**
     * validate resource by {@link java.io.InputStream}.
     * @param is the stream with the resource content
     * @return true if the document validates
     * @throws SAXParseException if the document is invalid
     */
    boolean validate(InputStream is) throws SAXParseException {
        schema.withInputStream { xsd ->
            SchemaFactory.newInstance( XMLConstants.W3C_XML_SCHEMA_NS_URI )
                    .newSchema( new StreamSource( xsd ) )
                    .newValidator()
                    .validate( new StreamSource( is ) )
        }
        true
    }
}
