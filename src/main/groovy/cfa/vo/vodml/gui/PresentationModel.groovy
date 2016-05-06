package cfa.vo.vodml.gui

import cfa.vo.vodml.metamodel.Model
import groovy.beans.Bindable
import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode

import javax.swing.tree.DefaultTreeModel

@Canonical()
@EqualsAndHashCode(includes=["modelDelegate",])
@Bindable
class PresentationModel {
    boolean dirty
    DefaultTreeModel treeModel
    private static ObjectGraphBuilder ogb = new ObjectGraphBuilder()
    @Delegate Model modelDelegate

    public PresentationModel() {
        this(new Model())
    }

    public PresentationModel(Model model) {
        this.modelDelegate = model
        ogb.classNameResolver = { name ->
            "javax.swing.tree.DefaultMutableTreeNode"
        }
        ogb.childPropertySetter = { parent, child, pname, cname ->
            parent.add( child )
        }
    }

    public DefaultTreeModel getTreeModel() {
        def root = ogb.node(userObject: this) {
            renderPackage(thisObject)
        }
        return new DefaultTreeModel(root)
    }

    @Override
    public String toString() {
        return modelDelegate.toString()
    }

    private renderPackage(obj) {
        def c = {
            node(userObject: "Primitive Types") {
                obj.primitiveTypes.each { ot ->
                    node(userObject: ot)
                }
            }
            node(userObject: "Enumerations") {
                obj.enumerations.each { ot ->
                    node(userObject: ot)
                }
            }
            node(userObject: "Data Types") {
                obj.dataTypes.each { ot ->
                    node(userObject: ot)
                }
            }
            node(userObject: "Object Types") {
                obj.objectTypes.each { ot ->
                    node(userObject: ot)
                }
            }
            node(userObject: "Packages") {
                obj.packages.each { pkg ->
                    node(userObject: pkg) {
                        renderPackage(pkg)
                    }
                }
            }
        }
        c.delegate = ogb
        c()
    }
}
