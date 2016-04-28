package cfa.vo.vodml.gui

import spock.lang.Specification

import static org.junit.Assert.fail

class ControllerSpec extends Specification {
    private Controller controller
    private String readPath
    private MainView view

    void setup() {
        view = Mock(MainView.class)
        controller = new Controller(view)
        readPath = this.class.getResource("/DatasetMetadata-1.0.vo-dml.xml").getFile()
    }

    def "test load"() {
        given:
            PresentationModel expected
            PresentationModel m

        when:
            expected = controller.load(readPath)

        then:
            1 * view.setProperty(*_) >> { args ->
                assert args[0] == "model"
                m = args[1]
            }
            assert m == expected
    }

    void testLoadFileNotFound() {
        try {
            controller.load(readPath+"DOESNTEXIST")
        } catch (FileNotFoundException e) {
            return // should fail
        }
        fail("Should have thown exception")
    }
}
