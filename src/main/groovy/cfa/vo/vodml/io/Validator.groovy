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
