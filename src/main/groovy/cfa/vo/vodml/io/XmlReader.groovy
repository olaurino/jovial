package cfa.vo.vodml.io

import cfa.vo.vodml.Model
import groovy.util.slurpersupport.GPathResult


class XmlReader {
    private slurper = new XmlSlurper()

    Model read(InputStream is) {
        def parsed = slurper.parse(is)
        return modelFrom(parsed)
    }

    Model read(File file) {
        read(new FileInputStream(file))
    }

    private Model modelFrom(GPathResult xml) {
        def model = new Model(
                name: xml.name,
                description: xml.description,
                title: xml.title,
                version: xml.version,
                lastModified: new Date(xml.lastModified.toString()),
                authors: xml.author,
        )
    }
}