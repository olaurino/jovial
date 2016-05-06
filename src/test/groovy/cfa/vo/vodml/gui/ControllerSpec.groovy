package cfa.vo.vodml.gui

import cfa.vo.vodml.io.VodmlReader
import spock.lang.Specification

import static org.junit.Assert.fail

class ControllerSpec extends Specification {
    private Controller controller
    private String readPath
    private MainView view
    private PresentationModel expected

    void setup() {
        view = Mock(MainView.class)
        controller = new Controller()
        readPath = this.class.getResource("/DatasetMetadata-1.0.vo-dml.xml").getFile()
        expected = new PresentationModel(new VodmlReader().read(new File(readPath)))
    }

    def "test load"() {
        when:
            PresentationModel actual = controller.load(readPath)

        then:
            assert actual == expected
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
