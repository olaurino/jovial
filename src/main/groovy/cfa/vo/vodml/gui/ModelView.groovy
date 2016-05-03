package cfa.vo.vodml.gui

import groovy.swing.SwingBuilder

import javax.swing.*
import javax.swing.event.DocumentListener
import java.awt.*

import static java.awt.GridBagConstraints.*

class ModelView extends JPanel {
    private SwingBuilder swing = new SwingBuilder();

    public ModelView(PresentationModel model) {
        JPanel panel = swing.panel(border: BorderFactory.createEtchedBorder()) {
            def defaultInsets = [5, 5, 5, 5]
            def labelConstraints = { order ->
                gbc(gridx: 0, gridy: order, anchor: WEST, insets: defaultInsets)
            }
            def fieldConstraints = { order ->
                gbc(gridx: 1, gridy: order, gridwidth: REMAINDER, weightx: 1,
                        fill: HORIZONTAL, anchor: EAST, insets: defaultInsets)
            }
            def fields = []

            def labelField = { order, name ->
                label("${name.capitalize()}:", constraints: labelConstraints(order))
                fields << textField(id: "${name}Field", text: model."${name}", constraints: fieldConstraints(order))
            }

            borderLayout()
            panel(border: BorderFactory.createTitledBorder("Model")) {
                gridBagLayout()
                labelField(0, "name")
                labelField(1, "title")
                labelField(2, "version")
                labelField(3, "description")
                labelField(4, "lastModified")

                // Authors
                label("Authors:", constraints: gbc(weighty: 1, gridx: 0, gridy: 5, insets: defaultInsets, fill: BOTH, anchor: WEST))
                panel(constraints: gbc(gridx: 1, gridy: 5, weightx: 1, fill: BOTH, gridwidth: REMAINDER, insets: defaultInsets, anchor: EAST)) {
                    borderLayout()
                    scrollPane() {
                        list(listData: model.authors)
                    }
                    panel(constraints: BorderLayout.EAST) {
                        vbox {
                            label(icon: new ImageIcon(getClass().getResource("/icons/list-add.png")))
                            label(icon: new ImageIcon(getClass().getResource("/icons/list-remove.png")))
                        }
                    }
                }

                // Imports
                label("Imports:", constraints: gbc(gridx: 0, gridy: 6, weighty: 1, insets: defaultInsets, fill: BOTH, anchor: WEST))
                panel(constraints: gbc(gridx: 1, gridy: 6, weighty: 1, weightx: 1,
                        fill: BOTH, insets: defaultInsets, anchor: EAST)) {
                    borderLayout()
                    scrollPane() {
                        table() {
                            tableModel(list: model.imports) {
                                propertyColumn(header: 'Name', propertyName: 'name')
                                propertyColumn(header: 'Version', propertyName: 'version')
                                propertyColumn(header: 'URL', propertyName: 'url')
                                propertyColumn(header: 'DocURL', propertyName: 'documentationURL')
                            }
                        }
                    }
                    panel(constraints: BorderLayout.EAST) {
                        vbox {
                            label(icon: new ImageIcon(getClass().getResource("/icons/list-add.png")))
                            label(icon: new ImageIcon(getClass().getResource("/icons/list-remove.png")))
                        }
                    }
                }
            }

            def updateModel = {
                println model
                if (model.description) {
                    println model.description
                }
            }
            fields.each {
                it.document.addDocumentListener(
                        [insertUpdate : updateModel,
                         removeUpdate : updateModel,
                         changedUpdate: updateModel] as DocumentListener)
            }

            bean(model, name: bind { nameField.text })
        }
        layout = new BorderLayout()
        add(panel)
    }
}
