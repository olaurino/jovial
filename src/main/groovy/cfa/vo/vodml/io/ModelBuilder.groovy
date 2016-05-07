package cfa.vo.vodml.io

import cfa.vo.vodml.VodmlException
import cfa.vo.vodml.metamodel.Attribute
import cfa.vo.vodml.metamodel.DataType
import cfa.vo.vodml.metamodel.Package

class ModelBuilder extends FactoryBuilderSupport {
    private static final String MODEL_PACKAGE = "cfa.vo.vodml.io.factories.model"

    public ModelBuilder() {
        registerBeanFactory("pack", Package)
        registerBeanFactory("package", Package)
        registerBeanFactory("dataType", DataType)
        registerBeanFactory("attribute", Attribute)
    }

    @Override
    protected Factory resolveFactory(name, Map attrs, value) {
        try {
            return Class.forName("$MODEL_PACKAGE.${name.capitalize()}Factory").newInstance()
        } catch (ClassNotFoundException | ClassCastException ex) {
            def ret = super.resolveFactory(name, attrs, value)
            if (ret == null) {
                throw new VodmlException("'$name' is not a valid node type", ex)
            } else {
                return ret
            }
        }
    }

    @Override
    protected void setParent(Object parent, Object child) {
        try {
            parent << child
        } catch (Exception ex) {
            throw new VodmlException("Cannot attach child $child to parent $parent. Does parent implement leftShift?", ex)
        }
    }
}
