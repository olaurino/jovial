package cfa.vo.vodml.gui

import ca.odell.glazedlists.gui.TableFormat
import ca.odell.glazedlists.swing.AdvancedListSelectionModel
import ca.odell.glazedlists.swing.GlazedListsSwing
import cfa.vo.vodml.metamodel.ModelImport
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
    private AdvancedListSelectionModel<ModelImport> importsSelectionModel
    private AdvancedListSelectionModel<String> authorsSelectionModel
    private defaultInsets = [5, 5, 5, 5]

    PresentationModel model

    public ModelView(PresentationModel model) {
        this.model = model
        labelConstraints.delegate = swing
        fieldConstraints.delegate = swing

        authorsSelectionModel = GlazedListsSwing.eventSelectionModelWithThreadProxyList(model.authors)
        importsSelectionModel = GlazedListsSwing.eventSelectionModelWithThreadProxyList(model.imports)
        def columnNames = ["name", "version", "url", "documentationURL"]
        def tableModel = GlazedListsSwing.eventTableModelWithThreadProxyList(model.imports, [
                getColumnCount: { return 4 },
                getColumnName: { int index ->
                    columnNames[index].capitalize()
                },
                getColumnValue: { ModelImport object, int index ->
                    object."${columnNames[index]}"
                }] as TableFormat)

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
                labelField(3, "lastModified")
                label("Description", constraints: labelConstraints(4))
                scrollPane(constraints: gbc(weighty:0.6, gridx:1, gridy:4, insets: defaultInsets, fill:BOTH, anchor: WEST)) {
                    fields << textArea(id: "descriptionField", rows: 20, text: model.description, wrapStyleWord: true, lineWrap: true, autoscrolls: true)
                }
                // Authors
                label("Authors:", constraints: gbc(weighty: 0.2, gridx: 0, gridy: 5, insets: defaultInsets, fill: BOTH, anchor: WEST))
                panel(id:"authors", constraints: gbc(gridx: 1, gridy: 5, weightx: 1, fill: BOTH, gridwidth: REMAINDER, insets: defaultInsets, anchor: EAST)) {
                    borderLayout()
                    scrollPane() {
                        list(selectionModel: authorsSelectionModel, model: GlazedListsSwing.eventListModelWithThreadProxyList(model.authors))
                    }
                    panel(constraints: BorderLayout.EAST) {
                        vbox {
                            button(border: null, action: addAuthorAction, name:"add", icon: new ImageIcon(getClass().getResource("/icons/list-add.png")))
                            button(border: null, action: removeAuthorAction, name:"remove", icon: new ImageIcon(getClass().getResource("/icons/list-remove.png")))
                        }
                    }
                }

                // Imports
                label("Imports:", constraints: gbc(gridx: 0, gridy: 6, weighty: 0.2, insets: defaultInsets, fill: BOTH, anchor: WEST))
                panel(id:"imports", constraints: gbc(gridx: 1, gridy: 6, weighty: 0.2, weightx: 1,
                        fill: BOTH, insets: defaultInsets, anchor: EAST)) {
                    borderLayout()
                    scrollPane() {
                        table(selectionModel: importsSelectionModel, model: tableModel)
                    }
                    panel(constraints: BorderLayout.EAST) {
                        vbox {
                            button(border: null, action: addImportAction, name:"add", icon: new ImageIcon(getClass().getResource("/icons/list-add.png")))
                            button(border: null, action: removeImportAction, name:"remove", icon: new ImageIcon(getClass().getResource("/icons/list-remove.png")))
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
            new URI(val)
        } catch (Exception ex) {
            status.warning("Cannot parse URI $val")
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

    private removeImportAction = swing.action(
            id:			 'removeImportAction',
            closure:     {
                if (!importsSelectionModel.selectionEmpty) {
                    model.imports.removeAll(importsSelectionModel.selected)
                }
            }
    )

    private removeAuthorAction = swing.action(
            id:			 'removeAuthorAction',
            closure:     {
                if (!authorsSelectionModel.selectionEmpty) {
                    model.authors.removeAll(authorsSelectionModel.selected)
                }
            }
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
                            button("OK", actionPerformed: {  model.authors.add(authorField.text);

/*
 * #%L
 * jovial
 * %%
 * Copyright (C) 2016 Smithsonian Astrophysical Observatory
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Smithsonian Astrophysical Observatory nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
dialog.dispose(); })
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
