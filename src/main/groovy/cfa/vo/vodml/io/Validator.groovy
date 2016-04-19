package cfa.vo.vodml.io

import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory

class Validator {
    private URL schema = getClass().getResource("/vodml-v1.0.xsd");

    boolean validate(URL file) {
        schema.withInputStream { xsd ->
            file.withInputStream { xml ->
                SchemaFactory.newInstance( XMLConstants.W3C_XML_SCHEMA_NS_URI )
                        .newSchema( new StreamSource( xsd ) )
                        .newValidator()
                        .validate( new StreamSource( xml ) )
            }
        }
        true
    }
}
