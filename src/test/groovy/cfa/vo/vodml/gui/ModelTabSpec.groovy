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

import org.uispec4j.Panel
import org.uispec4j.assertion.UISpecAssert

class ModelTabSpec extends GuiTestCase {

    def "tree root name is bound to model name"() {
        given:
        def modelTab = new ModelTab(new PresentationModel())
        Panel panel = new Panel(modelTab)

        when: "The model name is changed from the GUI"
        panel.getTextBox("nameField").text = "changed"

        then: "The tree root element changes accordingly"
        UISpecAssert.waitUntil(panel.tree.contentEquals("""
changed v1.0
  Primitive Types
  Enumerations
  Data Types
  Object Types
  Packages

"""), 1000)
    }

    def "tree root name is bound to model version"() {
        given:
        def modelTab = new ModelTab(new PresentationModel())
        Panel panel = new Panel(modelTab)

        when: "The model name is changed from the GUI"
        panel.getTextBox("versionField").text = "73.4"

        then: "The tree root element changes accordingly"
        UISpecAssert.waitUntil(panel.tree.contentEquals("""
my_model v73.4
  Primitive Types
  Enumerations
  Data Types
  Object Types
  Packages

"""), 1000)
    }
}
