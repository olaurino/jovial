package cfa.vo.vodml.io.factories.model

import cfa.vo.vodml.VodmlException
import cfa.vo.vodml.metamodel.ElementRef
import cfa.vo.vodml.metamodel.Multiplicity
import cfa.vo.vodml.metamodel.Role
import cfa.vo.vodml.utils.VodmlRef

abstract class RoleFactory extends AbstractVodmlFactory {
    @Override
    Object newInstance(String name, Map attributes) throws InstantiationException, IllegalAccessException {
        String multiplicity = attributes.remove("multiplicity")
        if (multiplicity) {
            try {
                def tokens = multiplicity.split("\\.\\.")
                def min = Integer.valueOf(tokens[0])
                def max
                if (tokens[1] == "-1" || tokens[1] == "*") {
                    max = -1
                } else {
                    max = Integer.valueOf(tokens[1])
                }
                attributes.multiplicity = [minOccurs: min, maxOccurs: max] as Multiplicity
            } catch (Exception ex) {
                throw new VodmlException(error(), ex)
            }
        }
        String dataType = attributes.remove("dataType").toString()
        if (!dataType.contains(":")) {
            dataType = dataType.replaceFirst("\\.", ":")
        }
        def role = getGenericType().newInstance(attributes)
        role.dataType = new ElementRef(vodmlref: new VodmlRef(dataType))
        return role
    }

    abstract Class<? extends Role> getGenericType()

    private error = {
        """
Illegal multiplicity expression. Please use '<minOccurs>..<maxOccurs>', where:
  <minOccurs> is a non negative integer, and
  <maxOccurs> is a positive integer, or '-1', or '*' for unbound multiplicity.
"""
    }
}
