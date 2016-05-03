package cfa.vo.vodml.gui

import cfa.vo.vodml.io.VodmlReader

class Controller {
    PresentationModel model

    public PresentationModel load(String path) throws FileNotFoundException {
        return new PresentationModel(new VodmlReader().read(new File(path)))
    }
}
