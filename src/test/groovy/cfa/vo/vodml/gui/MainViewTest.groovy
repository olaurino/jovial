package cfa.vo.vodml.gui

import org.uispec4j.UISpecTestCase
import org.uispec4j.interception.FileChooserHandler
import org.uispec4j.interception.MainClassAdapter
import org.uispec4j.interception.WindowInterceptor

class MainViewTest extends UISpecTestCase {
    private String path = getClass().getResource("/DatasetMetadata-1.0.vo-dml.xml").file

    @Override
    void setUp() {
        setAdapter(new MainClassAdapter(MainView.class))
    }

    void testMainEmpty() {
        mainWindow.titleEquals("VODML Model Builder").check()
        mainWindow.getTextBox("status").textEquals("No Model Selected").check()
    }

    void testLoad() {
        WindowInterceptor
                .init(mainWindow.menuBar.getMenu("File").getSubMenu("Load...").triggerClick())
                .process(FileChooserHandler.init().select(path))
                .run()

        mainWindow.getTabGroup("main").tabNamesEquals(["ds v0.x"] as String[]).check()
        mainWindow.tree.contentEquals("""
ds v0.x
  Primitive Types
  Enumerations
  Data Types
  Object Types
  Packages
    experiment
      Primitive Types
      Enumerations
      Data Types
      Object Types
        Observation
        ObsConfig
        BaseTarget
        Target
        AstroTarget
        Facility
        Instrument
        Proposal
        Characterisation
        Derived
        ObsDataset
        DerivedElement
        DerivedScalar
      Packages
    dataset
      Primitive Types
      Enumerations
        DataProductType
        CreationType
        RightsType
        SpectralBandType
      Data Types
      Object Types
        DataModel
        Dataset
        DataID
        Curation
        Publisher
        Contact
        Creator
        Contributor
        Publication
        Collection
      Packages
    party
      Primitive Types
      Enumerations
      Data Types
      Object Types
        Party
        Role
        Individual
        Organization
      Packages

""").check()
    }
}
