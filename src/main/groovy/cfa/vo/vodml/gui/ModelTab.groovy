package cfa.vo.vodml.gui

import groovy.swing.SwingBuilder

import javax.swing.*
import java.awt.BorderLayout

class ModelTab extends JPanel {
    private SwingBuilder swing = new SwingBuilder()
    PresentationModel model

    public ModelTab(PresentationModel model) {
        this.model = model
        JPanel panel = swing.panel() {
            borderLayout()
            splitPane(
                    leftComponent: scrollPane(minimumSize: [300, 600]) {
                        tree(model: model.treeModel)
                    },
                    rightComponent: new ModelView(model))
        }
        layout = new BorderLayout()
        add(panel, BorderLayout.CENTER)
    }
}
