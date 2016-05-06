package cfa.vo.vodml.gui

import cfa.vo.vodml.io.VodmlReader

class Controller {
    private status = StatusPanel.STATUS_LOGGER
    PresentationModel model

    public PresentationModel load(String path) throws FileNotFoundException {
        status.info("Reading ${path}...")
        def retVal = new PresentationModel(new VodmlReader().read(new File(path)))
        status.info("Successfully Read ${path}")
        return retVal
    }
}
