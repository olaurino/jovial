package cfa.vo.vodml.gui

import ca.odell.glazedlists.gui.TableFormat
import ca.odell.glazedlists.swing.DefaultEventTableModel
import ca.odell.glazedlists.swing.GlazedListsSwing
import cfa.vo.vodml.ModelImport
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
    private defaultInsets = [5, 5, 5, 5]

    PresentationModel model

    public ModelView(PresentationModel model) {
        this.model = model
        labelConstraints.delegate = swing
        fieldConstraints.delegate = swing

        JPanel panel = swing.panel(border: BorderFactory.createEtchedBorder()) {
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
                panel(id:"imports", constraints: gbc(gridx: 1, gridy: 6, weighty: 1, weightx: 1,
                        fill: BOTH, insets: defaultInsets, anchor: EAST)) {
                    borderLayout()
                    scrollPane() {
                        def columnNames = ["name", "version", "url", "documentationURL"]
                        table(model: new DefaultEventTableModel<ModelImport>( model.imports, [
                                getColumnCount: { return 4 },
                                getColumnName: { int index ->
                                    columnNames[index].capitalize()
                                },
                                getColumnValue: { ModelImport object, int index ->
                                    object."${columnNames[index]}"
                                }] as TableFormat))
                    }
                    panel(constraints: BorderLayout.EAST) {
                        vbox {
                            button(border: null, action: addImportAction, name:"add", icon: new ImageIcon(getClass().getResource("/icons/list-add.png")))
                            button(border: null, name:"remove", icon: new ImageIcon(getClass().getResource("/icons/list-remove.png")))
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

    private convertUrl = {String val ->
        try {
            new URL(val)
        } catch (Exception ex) {
            status.warning("Cannot parse URL $val")
        }
    }

    private addAuthorAction = swing.action(
            id:			 'addAuthorAction',
            closure:     this.&showAddAuthor
    )

    private addImportAction = swing.action(
            id:			 'addImportAction',
            closure:     this.&showAddImport
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

    private showAddImport = {
        swing.edt {
            ModelImport imp = new ModelImport()
            dialog(id: "dialog", title:"Add Author", modal:true, locationRelativeTo: MainView.frame, pack: true, show: true) {
                vbox {
                    panel(border: BorderFactory.createEmptyBorder(10, 10, 10, 10)) {
                        gridBagLayout()
                        label("Name: ", constraints: labelConstraints(0))
                        textField(id: "impNameField", constraints: fieldConstraints(0), columns: 20)
                        label("Version: ", constraints: labelConstraints(1))
                        textField(id: "impVersionField", constraints: fieldConstraints(1), columns: 20)
                        label("URL: ", constraints: labelConstraints(2))
                        textField(id: "impUrlField", constraints: fieldConstraints(2), columns: 20)
                        label("DocURL: ", constraints: labelConstraints(3))
                        textField(id: "impDocUrlField", constraints: fieldConstraints(3), columns: 20)
                    }
                    panel {
                        hbox {
                            button("OK", actionPerformed: { model.imports.add(imp); dialog.dispose() })
                            hstrut(width: 10)
                            button("Cancel", actionPerformed: { dialog.dispose()})
                        }
                    }
                }
                bean(imp,
                        name: bind { impNameField.text },
                        version: bind { impVersionField.text },
                        url: bind(converter: convertUrl) { impUrlField.text },
                        documentationURL: bind(converter: convertUrl) { impDocUrlField.text },
                )
            }
        }
    }

    def labelConstraints = { order ->
        gbc(gridx: 0, gridy: order, anchor: WEST, insets: defaultInsets)
    }

    def fieldConstraints = { order ->
        gbc(gridx: 1, gridy: order, gridwidth: REMAINDER, weightx: 1,
                fill: HORIZONTAL, anchor: EAST, insets: defaultInsets)
    }
}
