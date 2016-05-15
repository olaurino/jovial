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
package cfa.vo.vodml.gui

import groovy.swing.SwingBuilder

import javax.swing.*
import java.awt.*

/**
 * Main GUI Application class. It sets up the main frame for the application.
 */
class MainView {
    private swing = new SwingBuilder()
    JFileChooser chooser = swing.fileChooser()
    private controller = new Controller()
    static JFrame frame
    private StatusPanel statusPanel
    private JTabbedPane tabs

    private MainView() {
        swing.registerBeanFactory("statusPanel", StatusPanel)
        swing.registerFactory("modelTab", new ModelTabFactory())

        swing.edt {
            lookAndFeel('system')
            frame = frame(id: "root", title: 'VODML Model Builder', minimumSize: [800, 600], locationRelativeTo: null,
                    pack:true, show: true, defaultCloseOperation: JFrame.EXIT_ON_CLOSE,) {
                borderLayout()
                menuBar {
                    menu(mnemonic: 'F', text: 'File') {
                        menuItem(actionLoad)
                    }
                }
                tabs = tabbedPane(name: "main")
                statusPanel = statusPanel(constraints: BorderLayout.SOUTH)
            }
        }
        StatusPanel.STATUS_LOGGER.warning("No Model Selected")
    }

    /**
     * Overload left shift operator for opening models into the GUI
     *
     * @param model The model to be open.
     */
    def void leftShift(PresentationModel model) {
        swing.edt {
            String name = model.toString()
            ModelTab tab = swing.modelTab(model)
            tabs.addTab(name, tab)
            model.propertyChange = {
                swing.edt {
                    def i = tabs.getSelectedIndex()
                    tabs.setTitleAt(i, model.toString())
                }
            }
        }
    }

    /**
     * Main function.
     * @param argv If any arguments are passed to the command line, a test URL will be opened automatically.
     *      At this time the location of the test file is not portable.
     */
    public static void main(String[] argv) {
        MainView view = new MainView()
        if (argv) {
            Controller c = new Controller()
            view << c.load("C:/Users/Omar/IdeaProjects/jovial/src/test/resources/DatasetMetadata-1.0.vo-dml.xml")
        }
    }

    def actionLoad = swing.action(
            id:			 'actionLoad',
            name:        'Load...',
            closure:     this.&load,
            mnemonic:    'L',
            accelerator: 'F5'
    )

    def load = {
        int result = chooser.showOpenDialog(frame)
        if (result == JFileChooser.APPROVE_OPTION) {
            swing.doOutside {
                this << controller.load(chooser.getSelectedFile().absolutePath)
            }
        }
    }
}
