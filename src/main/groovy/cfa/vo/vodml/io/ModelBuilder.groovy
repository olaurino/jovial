package cfa.vo.vodml.io

import cfa.vo.vodml.VodmlException
import cfa.vo.vodml.io.factories.model.*
import cfa.vo.vodml.metamodel.*
import cfa.vo.vodml.utils.VodmlRef

class ModelBuilder extends FactoryBuilderSupport {
    private static final String MODEL_PACKAGE = "cfa.vo.vodml.io.factories.model"

    public ModelBuilder() {
        def packageFactory = new BeanFactory(Package)
        def importFactory = new ModelImportFactory()
        def simpleStringFactory = new SimpleStringFactory()
        registerFactory("primitiveType", new TypeFactory(PrimitiveType))
        registerFactory("dataType", new TypeFactory(DataType))
        registerFactory("objectType", new TypeFactory(ObjectType))
        registerFactory("pack", packageFactory)
        registerFactory("package", packageFactory)
        registerFactory("include", importFactory)
        registerFactory("import", importFactory)
        registerFactory("lastModified", new DateTimeFactory())
        registerFactory("enumeration", new TypeFactory(Enumeration_))
        registerFactory("literal", new BeanFactory(EnumLiteral))
        registerFactory("subsets", new SubsettedRoleFactory())
        ["title", "description", "author"].each {
            registerFactory(it, simpleStringFactory)
        }
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
