package cfa.vo.vodml.io.instance

import cfa.vo.vodml.instance.DataModelInstance
import groovy.xml.XmlUtil

abstract class AbstractMarkupInstanceWriter implements InstanceWriter {
    private DataModelInstance instance

    @Override
    void write(DataModelInstance instance, OutputStream os) {
        this.instance = instance
        def writer = new OutputStreamWriter(os)
        def builder = getMarkupBuilder().bind {
            mkp.xmlDeclaration()
            mkp.declareNamespace("${this.prefix}": this.nameSpace, xsi: "http://www.w3.org/2001/XMLSchema-instance")
            out << build(instance, delegate)
        }
        XmlUtil.serialize builder, writer
    }

    abstract void build(DataModelInstance instance, builder)

    abstract String getNameSpace()

    abstract String getPrefix()

    abstract getMarkupBuilder()
}
