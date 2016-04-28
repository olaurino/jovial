package cfa.vo.vodml.gui

import groovy.swing.SwingBuilder

import javax.swing.*

class MainView {
    PresentationModel model = new PresentationModel()
    private swing = new SwingBuilder()
    JFileChooser chooser = swing.fileChooser()
    private controller = new Controller(this)
    private JFrame frame
    private JTree tree

    public MainView() {
        swing.edt {
            frame = frame(title: 'VODML Model Builder', size: [800, 600], locationRelativeTo: null, show: true, defaultCloseOperation: JFrame.EXIT_ON_CLOSE) {
                menuBar {
                    menu(mnemonic: 'F', text: 'File') {
                        menuItem(actionLoad)
                    }
                }
                splitPane(leftComponent: widget(leftPanel()), rightComponent: widget(rightPanel()))
            }
        }
    }

    public void setModel(PresentationModel model) {
        this.model = model
        swing.doLater {
            tree.setModel(model.treeModel)
        }
    }

    public static void main(String[] argv) {
        new MainView()
    }

    def leftPanel = {
        swing.scrollPane(minimumSize: [300, 600]) {
            tree = tree(model: model.treeModel)
        }
    }

    def rightPanel = {
        swing.panel(size: [700, 600]) {

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
            controller.load(chooser.getSelectedFile().absolutePath)
        }
    }
}
