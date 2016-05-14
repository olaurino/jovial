package cfa.vo.vodml.gui

import cfa.vo.vodml.metamodel.Model
import cfa.vo.vodml.metamodel.PackageLike
import groovy.beans.Bindable
import groovy.transform.EqualsAndHashCode

import javax.swing.tree.DefaultTreeModel

@EqualsAndHashCode(excludes=["dirty",])
@Bindable
class PresentationModel extends Model {
    boolean dirty
    private static ObjectGraphBuilder ogb = new ObjectGraphBuilder()

    public PresentationModel() {
        this(new Model())
    }

    public PresentationModel(Model model) {
        decorate(model)
        ogb.classNameResolver = { name ->
            "javax.swing.tree.DefaultMutableTreeNode"
        }
        ogb.childPropertySetter = { parent, child, pname, cname ->
            parent.add( child )
        }
    }

    public DefaultTreeModel getTreeModel() {
        def root = ogb.node(userObject: this) {
            renderPackage(this)
        }
        return new DefaultTreeModel(root)
    }

    // Hack because Traits do not support AST trasformations, so @Delegate won't work.
    // Falling back on decorate constructor instead
    private decorate(Model model) {
        def properties = model.properties
        properties.remove("class")
        properties.remove("propertyChangeListeners")
        properties.each {
            this."$it.key" = it.value
        }
    }

    private renderPackage(PackageLike obj) {
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
