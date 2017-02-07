/*
 * #%L
 * jovial
 * %%
 * Copyright (C) 2016 - 2017 Smithsonian Astrophysical Observatory
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
package cfa.vo.vodml.instance.ui

import cfa.vo.vodml.io.ModelBuilder
import cfa.vo.vodml.io.ModelWriter
import cfa.vo.vodml.io.VoTableBuilder
import cfa.vo.vodml.io.instance.AltVotableWriter
import cfa.vo.vodml.io.instance.JsonVotableWriter
import cfa.vo.vodml.io.instance.VotableWriter
import cfa.vo.vodml.io.instance.YamlVotableWriter


class ModelGenerator {
    static void main(String[] args) {

        def modelString = '''
votable {
    model(vodmlURL: "http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/ds/DatasetMetadata-1.0.vo-dml.xml")
    model(vodmlURL: "http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/STC2/prototype/STCPrototype-2.0.vo-dml.xml")
    model(vodmlURL: "http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/characterization/Characterization.vo-dml.xml")
    model(vodmlURL: "http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/ivoa/IVOA.vo-dml.xml")

    object(id: "ACME", type: "ds:party.Organization") {
        value(role: "name", value: "ACME edu")
        value(role: "address", value: "565 N Clinton Drive, Milwaukee, WI")
        value(role: "phone", value: "555-012-3456")
        value(role: "email", value: "helpdesk@acme.org")
        value(role: "logo", value: "http://acme.org/stunning.png")
    }

    table() {
      object(type: "ds:experiment.Observation") {
          column(role: "observationID", value: "OBSID")
          collection(role: "obsConfig") {
              object() {
                  column(role: "bandpass", value: "BANDPASS")
                  column(role: "datasource", value: "DATASOURCE")
                  collection(role: "facility") {
                      object() {
                          reference(role: "party", value: "ACME")
                      }
                  }
              }
          }
      }
    }
}
'''
        generateInstance(modelString)
    }

    static void generate(String modelString) {
        def builder = new ModelBuilder()
        def binding = new Binding(model: builder.&script)
        def shell = new GroovyShell(ModelGenerator.class.classLoader, binding)
        def model = shell.evaluate modelString
        def writer = new ModelWriter()
        writer.write(model, System.out)
    }

    static void generateInstance(String instanceString) {
        def builder = new VoTableBuilder()
        def binding = new Binding(votable: builder.&script)
        def shell = new GroovyShell(ModelGenerator.class.classLoader, binding)
        def instance = shell.evaluate instanceString
        def writer = new VotableWriter()
        writer.write(instance, System.out)
    }

    static void generateAltInstance(String instanceString) {
        def builder = new VoTableBuilder()
        def binding = new Binding(votable: builder.&script)
        def shell = new GroovyShell(ModelGenerator.class.classLoader, binding)
        def instance = shell.evaluate instanceString
        def writer = new AltVotableWriter()
        writer.write(instance, System.out)
    }

    static void generateJsonInstance(String instanceString) {
        def builder = new VoTableBuilder()
        def binding = new Binding(votable: builder.&script)
        def shell = new GroovyShell(ModelGenerator.class.classLoader, binding)
        def instance = shell.evaluate instanceString
        def writer = new JsonVotableWriter()
        writer.write(instance, System.out)
    }

    static void generateYamlInstance(String instanceString) {
        def builder = new VoTableBuilder()
        def binding = new Binding(votable: builder.&script)
        def shell = new GroovyShell(ModelGenerator.class.classLoader, binding)
        def instance = shell.evaluate instanceString
        def writer = new YamlVotableWriter()
        writer.write(instance, System.out)
    }
}
