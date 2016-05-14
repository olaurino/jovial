package cfa.vo.vodml.io.factories.model

import cfa.vo.vodml.metamodel.ReferableElement

class BeanFactory extends AbstractVodmlFactory {
    private Class<? extends ReferableElement> clazz

    public BeanFactory(Class<? extends ReferableElement> clazz) {
        this.clazz = clazz
    }

    @Override
    def newInstance(String name, Map attributes) {
        return clazz.newInstance(attributes)
    }
}
