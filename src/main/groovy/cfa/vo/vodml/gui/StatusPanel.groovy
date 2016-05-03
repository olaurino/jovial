package cfa.vo.vodml.gui

import groovy.swing.SwingBuilder

import javax.swing.*

class StatusPanel extends JPanel {
    private JLabel statusLabel
    private SwingBuilder swing = new SwingBuilder()

    public StatusPanel() {
        swing.build {
            statusLabel = label(id: "status")
        }
        setBorder(BorderFactory.createBevelBorder(1))
        add(statusLabel)
    }

    public void setStatus(String status) {
        statusLabel.text = status
    }
}
