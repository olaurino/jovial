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

    void testMain() {
        mainWindow.titleEquals("VODML Model Builder").check()
        mainWindow.tree.contentEquals("""
my_model v1.0
  Primitive Types
  Enumerations
  Data Types
  Object Types
  Packages
""").check()
    }

    void testLoad() {
        WindowInterceptor
                .init(mainWindow.menuBar.getMenu("File").getSubMenu("Load...").triggerClick())
                .process(FileChooserHandler.init().select(path))
                .run()

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
