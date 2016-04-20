package cfa.vo.vodml.io

import cfa.vo.vodml.Model
import cfa.vo.vodml.instance.Instance
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil

class XmlWriter {

    def write(model, OutputStream os) {
        def writer = new OutputStreamWriter(os)
        def builder = new StreamingMarkupBuilder().bind {
            mkp.xmlDeclaration()
            mkp.declareNamespace("${model.prefix}": model.ns, xsi: "http://www.w3.org/2001/XMLSchema-instance")
            out << model
        }
        XmlUtil.serialize builder, writer
    }

//    def write(Instance instance, OutputStream os) {
//        def writer = new OutputStreamWriter(os)
//        def builder = new StreamingMarkupBuilder().bind {
//            mkp.xmlDeclaration()
//            mkp.declareNamespace("${instance.prefix}": instance.ns, xsi: "http://www.w3.org/2001/XMLSchema-instance")
//            out << instance
//        }
//        XmlUtil.serialize builder, writer
//    }
}