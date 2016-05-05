package cfa.vo.vodml.gui

import groovy.swing.SwingBuilder

import javax.swing.*
import java.awt.*
import java.util.logging.Handler
import java.util.logging.LogRecord
import java.util.logging.Logger

/**
 * Specialization of {@link JPanel} for a Status Panel that is updated through a special logger.
 *
 * Simply grab the logger from {@link #STATUS_LOGGER} and log to it as usual. Messages will be used
 * to set the text of this panel's status label.
 *
 * For convenience, a {@link #setStatus(String)} setter is provided for setting the status text directly.
 * However, this should be used only to
 */
class StatusPanel extends JPanel {
    private JLabel statusLabel
    private SwingBuilder swing = new SwingBuilder()
    public static final Logger STATUS_LOGGER = Logger.getLogger("status")

    public StatusPanel() {
        swing.build {
            statusLabel = label(name: "status", border: BorderFactory.createEmptyBorder(5,5,5,5))
        }
        layout = new BorderLayout()
        border = BorderFactory.createBevelBorder(1)
        add(statusLabel)

        STATUS_LOGGER.addHandler(new StatusHandler())
    }

    public void setStatus(String status) {
        statusLabel.text = status
    }

    private class StatusHandler extends Handler {

        @Override
        void publish(LogRecord record) {
            swing.edt {
                statusLabel.text = "${record.level}: ${record.message}"
            }
        }

        @Override
        void flush() {
        }

        @Override
        void close() throws SecurityException {
        }
    }
}
