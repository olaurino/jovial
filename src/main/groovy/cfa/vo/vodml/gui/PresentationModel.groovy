/*
 * #%L
 * jovial
 * %%
 * Copyright (C) 2016 Smithsonian Astrophysical Observatory
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Smithsonian Astrophysical Observatory nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
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
