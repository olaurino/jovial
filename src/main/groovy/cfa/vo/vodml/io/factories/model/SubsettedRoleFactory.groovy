package cfa.vo.vodml.io.factories.model

import cfa.vo.vodml.metamodel.ElementRef
import cfa.vo.vodml.metamodel.SubsettedRole
import cfa.vo.vodml.utils.VodmlRef

class SubsettedRoleFactory extends AbstractFactory {

    @Override
    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        String roleS = attributes.remove("role")
        String dataTypeS = attributes.remove("dataType")
        def roleAndType = [roleS, dataTypeS].collect {
            if (!it.contains(":")) {
                it.replaceFirst("\\.", ":")
            } else {
                it
            }
        }
        VodmlRef roleRef = new VodmlRef(roleAndType[0])
        VodmlRef dataTypeRef = new VodmlRef(roleAndType[1])
        ElementRef role = new ElementRef(vodmlref: roleRef)
        ElementRef dataType = new ElementRef(vodmlref: dataTypeRef)
        SubsettedRole subsettedRole = attributes as SubsettedRole
        subsettedRole.role = role
        subsettedRole.dataType = dataType
        return subsettedRole
    }
}
