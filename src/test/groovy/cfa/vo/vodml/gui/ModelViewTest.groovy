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

import ca.odell.glazedlists.BasicEventList
import cfa.vo.vodml.metamodel.ModelImport
import org.joda.time.DateTime
import org.uispec4j.Panel
import org.uispec4j.UISpec4J
import org.uispec4j.Window
import org.uispec4j.assertion.UISpecAssert
import org.uispec4j.interception.WindowInterceptor
import spock.lang.Specification

class ModelViewTest extends Specification {
    private PresentationModel model
    private ModelView view
    private Panel panel

    def setup() {
        UISpec4J.init()
        model = [name: "test", version: "1.0", description: "A description",
        title: "Test Model", lastModified: new DateTime("2016-02-14T10:30Z"),
        authors: ["Titius", "Caius", "Sempronius"] as BasicEventList,
        imports: [
                new ModelImport(name: "model1", version: "1.1", url: new URI("http://some"), documentationURL: new URI("http://some.doc")),
                new ModelImport(name: "model2", version: "2.1", url: new URI("http://other"), documentationURL: new URI("http://other.doc")),
        ] as BasicEventList
        ] as PresentationModel
        view = new ModelView(model)
        panel = new Panel(view)
    }

    def "test instantiation"() {
        expect:
        view.model == model
        text("nameField") == "test"
        text("descriptionField") == "A description"
        text("versionField") == "1.0"
        text("titleField") == "Test Model"
        text("lastModifiedField") == "2016-02-14T10:30:00.000Z"
        panel.listBox.contentEquals("Titius", "Caius", "Sempronius").check()
        panel.table.contentEquals(
                ["name", "version", "url", "documentationurl"] as String[],
                [["model1", "1.1", "http://some", "http://some.doc"],
                 ["model2", "2.1", "http://other", "http://other.doc"]] as String[][])
                .check()
    }

    def "test binding of text fields"() {
        when:
        panel.getTextBox("nameField").text = "new"
        panel.getTextBox("descriptionField").text = "Different"
        panel.getTextBox("versionField").text = "3.5"
        panel.getTextBox("titleField").text = "Another"
        panel.getTextBox("lastModifiedField").text = "2011-01-30T9:45"

        then:
        view.model.name == "new"
        view.model.description == "Different"
        view.model.version == "3.5"
        view.model.title == "Another"
        view.model.lastModified == new DateTime("2011-01-30T9:45")
    }

    def "test binding of authors list"() {
        when:
        WindowInterceptor.init(panel.getPanel("authors").getButton("add").triggerClick())
                .process { Window window ->
            window.getInputTextBox().text = "Iulius"
            window.getButton("OK").triggerClick()
        }.run()
        then:
        view.model.authors == ["Titius", "Caius", "Sempronius", "Iulius"]
    }

    def "test binding of model imports table"() {
        when:
        WindowInterceptor.init(panel.getPanel("imports").getButton("add").triggerClick())
                .process { Window window ->
            window.getInputTextBox("nameField").text = "someImport"
            window.getInputTextBox("versionField").text = "1.0"
            window.getInputTextBox("impUrlField").text = "https://ideas"
            window.getInputTextBox("impDocUrlField").text = "https://ideas.doc"
            window.getButton("OK").triggerClick()
        }.run()
        then:
        panel.table.contentEquals(
                ["name", "version", "url", "documentationUrl"] as String[],
                [["model1", "1.1", "http://some", "http://some.doc"],
                 ["model2", "2.1", "http://other", "http://other.doc"],
                 ["someImport", "1.0", "https://ideas", "https://ideas.doc"]
                ] as String[][])
                .true
    }

    def "test remove model imports"() {
        when:
        Panel imp = panel.getPanel("imports")
        imp.table.selectAllRows()
        imp.getButton("remove").click()
        then:
        UISpecAssert.waitUntil(imp.table.isEmpty(), 1000)
    }

    def "test remove authors"() {
        when:
        Panel imp = panel.getPanel("authors")
        imp.listBox.selectIndices(0,1,2)
        imp.getButton("remove").click()
        then:
        UISpecAssert.waitUntil(imp.listBox.isEmpty(), 1000)
    }

    def text = {String name ->
        panel.getTextBox(name).text
    }
}
