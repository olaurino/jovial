package cfa.vo.vodml.gui

import cfa.vo.vodml.io.VodmlReader

class Controller {
    PresentationModel model
    private MainView view

    public Controller(MainView view) {
        this.view = view
    }

    public PresentationModel load(String path) throws FileNotFoundException {
        model = new PresentationModel(new VodmlReader().read(new File(path)))
        view.model = model
        return model
    }
}
