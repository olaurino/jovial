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

import org.uispec4j.UISpecTestCase
import org.uispec4j.assertion.UISpecAssert
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
        mainWindow.getTextBox("status").textEquals("WARNING: No Model Selected").check()
    }

    void testLoad() {
        WindowInterceptor
                .init(mainWindow.menuBar.getMenu("File").getSubMenu("Load...").triggerClick())
                .process(FileChooserHandler.init().select(path))
                .run()

        UISpecAssert.waitUntil(mainWindow.getTabGroup("main").tabNamesEquals(["ds v0.x"] as String[]), 1000)
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
