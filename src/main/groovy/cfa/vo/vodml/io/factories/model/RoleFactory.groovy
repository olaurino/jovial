package cfa.vo.vodml.io.factories.model

import cfa.vo.vodml.metamodel.ElementRef
import cfa.vo.vodml.metamodel.Role
import cfa.vo.vodml.utils.VodmlRef

abstract class RoleFactory extends AbstractVodmlFactory {
    @Override
    Object newInstance(Map attributes) throws InstantiationException, IllegalAccessException {
        String dataType = attributes.remove("dataType").toString()
        if(!dataType.contains(":")) {
            dataType = dataType.replaceFirst("\\.", ":")
        }
        def role = newInstance(getGenericType(), attributes)
        role.dataType = new ElementRef(vodmlref: new VodmlRef(dataType))
        return role
    }

    abstract Class<? extends Role> getGenericType()

    def newInstance(Class<? extends Role> clazz, arguments) {
        clazz.newInstance(arguments)
    }
}
