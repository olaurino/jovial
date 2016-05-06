package cfa.vo.vodml.gui

import spock.lang.Specification


class ModelTabFactoryTest extends Specification {
    private FactoryBuilderSupport fbs = Stub(FactoryBuilderSupport)
    private PresentationModel model = new PresentationModel()
    private ModelTabFactory factory = new ModelTabFactory()

    def "Invoke with value only"(){
        when: "factory method is invoked with value only"
            def tab = factory.newInstance(fbs, "name", model, null)

        then: "tab is an instance of ModelTab with correct model"
            assert tab instanceof ModelTab
            assert tab.model == model
    }

    def "Invoke with value and attributes"() {
        when: "factory method is invoked with value and attributes"
            factory.newInstance(fbs, "name", model, [attr: "whatever"])

        then: "exception is thrown"
            def ex = thrown(InstantiationException)
            ex.message == "No attributes allowed in building element. Only a value accepted"
    }

    def "Invoke with null value"() {
        when: "factory method is invoked with null value"
            factory.newInstance(fbs, "name", null, null)

        then: "exception is thrown"
            def ex = thrown(InstantiationException)
            ex.message == "Please provide a PresentationModel value, none was provided"
    }
}
