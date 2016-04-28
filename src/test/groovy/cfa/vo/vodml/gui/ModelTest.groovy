package cfa.vo.vodml.gui

import cfa.vo.vodml.io.VodmlReader
import org.junit.Before
import org.junit.Test

import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel

class ModelTest {
    private PresentationModel model

    @Before
    public void setUp() {
        model = new PresentationModel(new VodmlReader().read(getClass().getResource("/DatasetMetadata-1.0.vo-dml.xml").openStream()))
    }

    @Test
    void testGetTreeModel() {
        DefaultTreeModel tree = model.treeModel
        DefaultMutableTreeNode root = tree.getRoot()
        assert root.getUserObject() == model
        assert root.getLastChild().getUserObject() == "Packages"
        (0..2).each {
            assert root.lastChild.getChildAt(it).userObject.name == model.packages.get(it).name
        }
    }
}
