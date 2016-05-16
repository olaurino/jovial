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

import ca.odell.glazedlists.BasicEventList
import cfa.vo.vodml.gui.GuiTestCase
import cfa.vo.vodml.gui.PresentationModel
import cfa.vo.vodml.metamodel.*
import groovy.swing.SwingBuilder
import org.uispec4j.Panel
import org.uispec4j.assertion.UISpecAssert

import javax.swing.*
import javax.swing.tree.TreeSelectionModel

class PresentationModelTreeModelSpec extends GuiTestCase {
    private Panel panel
    private PresentationModel pModel

    def setup() {
        Model model = new Model()
        pModel = new PresentationModel(model)
        PresentationModelTreeModel treeModel = new PresentationModelTreeModel(pModel)
        JScrollPane jPanel
        JTree jTree

        new SwingBuilder().edt {
            jPanel = scrollPane() {
                jTree = tree(model: treeModel, showsRootHandles: true)
            }
        }

        jTree.selectionModel.selectionMode = TreeSelectionModel.SINGLE_TREE_SELECTION

        panel = new Panel(jPanel)
    }

    def "test basic info"() {
        expect:
        panel.tree.contentEquals(expectedInitialTree).check()
    }

    def "test binding of packages"() {
        when:
        pModel.packages.add([name: "newPackage"] as Package)

        then:
        UISpecAssert.waitUntil(panel.tree.contentEquals(treePackage), 1000)
    }

    def "test nested packages"() {
        when:
        pModel.packages.add([name: "newPackage", packages: [[name: "nestedPackage"] as Package] as BasicEventList] as Package)

        then:
        UISpecAssert.waitUntil(panel.tree.contentEquals(treeNestedPackage), 1000)
    }

    def "test binding of primitive types"() {
        when:
        pModel.primitiveTypes.add([name: "aPrimitiveType"] as PrimitiveType)

        then:
        UISpecAssert.waitUntil(panel.tree.contentEquals(treePrimitiveType), 1000)
    }

    def "test binding of enumerations"() {
        when:
        pModel.enumerations.add(createEnumeration())

        then:
        UISpecAssert.waitUntil(panel.tree.contentEquals(enumerationTree), 1000)
    }

    def "test binding of dataTypes"() {
        when:
        pModel.dataTypes.add([name: "aDataType", ] as DataType)

        then:
        UISpecAssert.waitUntil(panel.tree.contentEquals(dataTypeTree), 1000)
    }

    def "test binding of objectTypes"() {
        when:
        pModel.objectTypes.add([name: "anObjectType"] as ObjectType)

        then:
        UISpecAssert.waitUntil(panel.tree.contentEquals(objectTypeTree), 1000)
    }

    private Enumeration_ createEnumeration() {
        Enumeration_ enumeration = new Enumeration_(name: "anEnumeration")
        enumeration.literals.add([name: "A"] as EnumLiteral)
        enumeration.literals.add([name: "B"] as EnumLiteral)
        return enumeration
    }

    def expectedInitialTree = """
my_model v1.0
  Primitive Types
  Enumerations
  Data Types
  Object Types
  Packages
"""

    def treePackage = """
my_model v1.0
  Primitive Types
  Enumerations
  Data Types
  Object Types
  Packages
    newPackage
      Primitive Types
      Enumerations
      Data Types
      Object Types
      Packages
"""

    def treeNestedPackage = """
my_model v1.0
  Primitive Types
  Enumerations
  Data Types
  Object Types
  Packages
    newPackage
      Primitive Types
      Enumerations
      Data Types
      Object Types
      Packages
        nestedPackage
          Primitive Types
          Enumerations
          Data Types
          Object Types
          Packages
"""

    def treePrimitiveType = """
my_model v1.0
  Primitive Types
    aPrimitiveType
  Enumerations
  Data Types
  Object Types
  Packages
"""

    def enumerationTree = """
my_model v1.0
  Primitive Types
  Enumerations
    anEnumeration
  Data Types
  Object Types
  Packages
"""

    def dataTypeTree = """
my_model v1.0
  Primitive Types
  Enumerations
  Data Types
    aDataType
  Object Types
  Packages
"""

    def objectTypeTree = """
my_model v1.0
  Primitive Types
  Enumerations
  Data Types
  Object Types
    anObjectType
  Packages
"""
}
