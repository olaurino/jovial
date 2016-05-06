package cfa.vo.vodml.gui

import org.uispec4j.Panel
import org.uispec4j.assertion.UISpecAssert
import spock.lang.Specification

class StatusPanelTest extends Specification {
    private StatusPanel panel
    private Panel uiPanel

    def setup() {
        panel = new StatusPanel()
        uiPanel = new Panel(panel)
    }

    def "test setStatus method updates status"() {
        when:
        panel.status = "Some Status"
        then:
        statusAssertion("Some Status").check()
    }

    def "test status updates when logging remotely"() {
        when:
        StatusPanel.STATUS_LOGGER.info("message")
        then:
        UISpecAssert.waitUntil(statusAssertion("INFO: message"), 1000)
    }

    def statusAssertion = { message ->
        uiPanel.getTextBox("status").textEquals(message)
    }
}
