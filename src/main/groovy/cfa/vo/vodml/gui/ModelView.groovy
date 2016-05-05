package cfa.vo.vodml.gui

import ca.odell.glazedlists.swing.GlazedListsSwing
import groovy.swing.SwingBuilder
import org.joda.time.DateTime

import javax.swing.*
import javax.swing.event.DocumentListener
import java.awt.*
import java.util.logging.Logger

import static java.awt.GridBagConstraints.*

class ModelView extends JPanel {
    private SwingBuilder swing = new SwingBuilder()
    private Logger status = StatusPanel.STATUS_LOGGER
    PresentationModel model

    public ModelView(PresentationModel model) {
        this.model = model

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
                panel(id:"authors", constraints: gbc(gridx: 1, gridy: 5, weightx: 1, fill: BOTH, gridwidth: REMAINDER, insets: defaultInsets, anchor: EAST)) {
                    borderLayout()
                    scrollPane() {
                        list(model: GlazedListsSwing.eventListModelWithThreadProxyList(model.authors))
                    }
                    panel(constraints: BorderLayout.EAST) {
                        vbox {
                            button(border: null, action: addAuthorAction, name:"add", icon: new ImageIcon(getClass().getResource("/icons/list-add.png")))
                            button(border: null, name:"remove", icon: new ImageIcon(getClass().getResource("/icons/list-remove.png")))
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

            }
            fields.each {
                it.document.addDocumentListener(
                        [insertUpdate : updateModel,
                         removeUpdate : updateModel,
                         changedUpdate: updateModel] as DocumentListener)
            }

            bean(model,
                    name: bind { nameField.text },
                    description: bind { descriptionField.text },
                    version: bind { versionField.text },
                    title: bind { titleField.text },
                    lastModified: bind(converter: convertDate) { lastModifiedField.text },
            )
        }
        layout = new BorderLayout()
        add(panel)
    }

    private convertDate = {String date ->
        try {
            def retVal = new DateTime(date)
            status.info("Parsed date $date")
            retVal
        } catch (Exception ex) {
            status.warning("Cannot parse date $date")
        }
    }

    private addAuthorAction = swing.action(
            id:			 'actionLoad',
            closure:     this.&showAddAuthor
    )

    private showAddAuthor = {
        swing.edt {
            dialog(id: "dialog", title:"Add Author", modal:true, locationRelativeTo: MainView.frame, pack: true, show: true) {
                panel(border: BorderFactory.createEmptyBorder(10,10,10,10)) {
                    vbox {
                        hbox {
                            label("Author: ")
                            textField(id: "authorField", columns: 30)
                        }
                        vstrut(height: 10)
                        hbox {
                            button("OK", actionPerformed: {  model.authors.add(authorField.text); dialog.dispose(); })
                            hstrut(width: 10)
                            button("Cancel", actionPerformed: { dialog.dispose()})
                        }
                    }
                }
            }
        }
    }
}
