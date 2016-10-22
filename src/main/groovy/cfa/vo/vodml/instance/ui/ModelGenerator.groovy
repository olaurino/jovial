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
