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
package cfa.vo.vodml.gui.tree

import cfa.vo.vodml.gui.GuiTestCase
import cfa.vo.vodml.gui.PresentationModel
import cfa.vo.vodml.metamodel.Model
import cfa.vo.vodml.metamodel.Package
import groovy.swing.SwingBuilder
import org.uispec4j.Trigger
import org.uispec4j.Window
import org.uispec4j.interception.PopupMenuInterceptor
import org.uispec4j.interception.WindowInterceptor

class PresentationModelJTreeSpec extends GuiTestCase {
    private Window window
    private PresentationModel pModel

    def setup() {
        def swing = new SwingBuilder()
        swing.registerBeanFactory("tree", PresentationModelJTree)
        Model model = new Model()
        pModel = new PresentationModel(model)
        PresentationModelTreeModel treeModel = new PresentationModelTreeModel(pModel)

        WindowInterceptor
                .init({
                    swing.edt {
                        frame(show:true, pack:true) {
                            scrollPane() {
                                tree(model: treeModel, showsRootHandles: false)
                            }
                        }
                    }
                })
                .process({w-> window = w; return Trigger.DO_NOTHING})
                .run()
    }

    def "test single selection"() {
        when:
        window.tree.select(["Primitive Types", "Enumerations"] as String[])

        then:
        window.tree.selectionEquals(["Primitive Types",] as String[]).check()
    }

    def "test tree is restored to previous state after structure change"() {
        given:
        pModel.packages.add(new Package(name: "aPackage"))
        window.tree.expandAll()

        when:
        pModel.packages.add(new Package(name: "anotherPackage"))

        then:
        window.tree.pathIsExpanded("Packages").check()
    }

    def "test packageList popup menu"(String name) {
        expect:
        PopupMenuInterceptor
                .run(window.tree.triggerRightClick("${name}s"))
                .contentEquals(
                    ["Add $name"] as String[])
                .check()
        where:
        name  | _
        "Package" | _
        "Primitive Type" | _
        "Enumeration" | _
        "Data Type" | _
        "Object Type" | _
    }

    def "test packageLike popup menu"() {
        given:
        pModel.packages.add(new Package(name: "aPackage"))

        expect:
        PopupMenuInterceptor
                .run(window.tree.triggerRightClick("Packages/aPackage"))
                .contentEquals(
                ["Primitive Type", "Enumeration", "Data Type", "Object Type", "Package"].collect {
                    "Add $it"
                } as String[])
                .check()
    }
}
