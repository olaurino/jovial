package cfa.vo.vodml.io

import cfa.vo.vodml.VodmlException
import cfa.vo.vodml.io.factories.model.BeanFactory
import cfa.vo.vodml.io.factories.model.DateTimeFactory
import cfa.vo.vodml.io.factories.model.ModelImportFactory
import cfa.vo.vodml.io.factories.model.SimpleStringFactory
import cfa.vo.vodml.metamodel.Package
import cfa.vo.vodml.metamodel.EnumLiteral
import cfa.vo.vodml.metamodel.Enumeration_
import cfa.vo.vodml.utils.VodmlRef

class ModelBuilder extends FactoryBuilderSupport {
    private static final String MODEL_PACKAGE = "cfa.vo.vodml.io.factories.model"

    public ModelBuilder() {
        def packageFactory = new BeanFactory(Package)
        def importFactory = new ModelImportFactory()
        def simpleStringFactory = new SimpleStringFactory()
        registerFactory("pack", packageFactory)
        registerFactory("package", packageFactory)
        registerFactory("include", importFactory)
        registerFactory("import", importFactory)
        ["title", "description", "author"].each {
            registerFactory(it, simpleStringFactory)
        }
        registerFactory("lastModified", new DateTimeFactory())
        registerFactory("enumeration", new BeanFactory(Enumeration_))
        registerFactory("literal", new BeanFactory(EnumLiteral))
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

    @Override
    Object getVariable(String name) {
        try {
            super.getVariable(name)
        } catch (MissingPropertyException ignored) {
            new VodmlRef(name, "")
        }
    }
}
