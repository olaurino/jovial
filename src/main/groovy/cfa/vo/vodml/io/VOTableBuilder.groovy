package cfa.vo.vodml.io


class VOTableBuilder extends BuilderSupport {

    private static final String MODEL_PACKAGE = "cfa.vo.vodml.instance"

    private static final Map SUPPORTED_MODELS = [build: ArrayList,].withDefault {
        Class.forName("${MODEL_PACKAGE}.${it.capitalize()}")
    }

    @Override
    protected void setParent(Object parent, Object child) {
        if (parent == child) return
        child.parent = parent
        child.resolver = parent.resolver
        parent << child
    }

    @Override
    protected Object createNode(Object name) {
        return createNode(name, null, null)
    }

    @Override
    protected Object createNode(Object name, Object value) {
        return createNode(name, null, value)
    }

    @Override
    protected Object createNode(Object name, Map attrs) {
        return createNode(name, attrs, null)
    }

    @Override
    protected Object createNode(Object name, Map attrs, Object value) {
        try {
            def instance
            if (!value) {
                instance = SUPPORTED_MODELS[name].newInstance()
            } else {
                instance = SUPPORTED_MODELS[name].newInstance(value)
            }
            instance.init(attrs)
            return instance
        }
        catch (ClassNotFoundException ex) {
            if (value && !attrs) {
                current."$name" = value
                return current
            } else {
                throw ex
            }
        }
    }

    @Override
    protected void nodeCompleted(Object parent, Object node) {
        node.finish()
    }
}
