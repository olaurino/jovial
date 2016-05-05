package cfa.vo.vodml.gui

import groovy.swing.SwingBuilder

import javax.swing.*
import java.awt.*

class MainView {
    private swing = new SwingBuilder()
    JFileChooser chooser = swing.fileChooser()
    private controller = new Controller()
    private JFrame frame
    private StatusPanel statusPanel
    private JTabbedPane tabs

    public MainView() {
        swing.registerBeanFactory("statusPanel", StatusPanel)
        swing.registerFactory("modelTab", new ModelTabFactory())

        swing.edt {
            lookAndFeel('system')
            frame = frame(title: 'VODML Model Builder', minimumSize: [800, 600], locationRelativeTo: null,
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

    def void leftShift(PresentationModel model) {
        swing.edt {
            String name = model.toString()
            JComponent tab = swing.modelTab(model)
            tabs.addTab(name, tab)
        }
    }

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
