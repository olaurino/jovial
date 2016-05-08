package cfa.vo.vodml.metamodel

import ca.odell.glazedlists.BasicEventList
import ca.odell.glazedlists.EventList
import cfa.vo.vodml.io.factories.model.StringAttribute
import cfa.vo.vodml.utils.VodmlRef
import groovy.beans.Bindable
import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode
import org.joda.time.DateTime

@Bindable
@Canonical
@EqualsAndHashCode(excludes = "lastModified")
class Model implements Buildable, PackageLike {
    String prefix = "vo-dml"
    String ns = "http://www.ivoa.net/xml/VODML/v1.0"
    String name = "my_model"
    String title = "My Model"
    String version = "1.0"
    DateTime lastModified = new DateTime()
    String description
    EventList<String> authors = [] as BasicEventList
    List<URI> previousVersions = []
    EventList<ModelImport> imports = [] as BasicEventList

    @Override
    String toString() {
        return "$name v$version"
    }

    void leftShift(DateTime child) {
        this.lastModified = child
    }

    void leftShift(StringAttribute attr) {
        switch(attr.name) {
            case("author"):
                authors.add(attr.value)
                break
            default:
                this."$attr.name" = attr.value
        }
    }

    void leftShift(ModelImport child) {
        imports << child
    }

    def propagateVodmlid(ReferableElement child) {
        if (child.vodmlid == null) {
            child.vodmlid = new VodmlRef(child.name)
        }
    }

    @Override
    void build(GroovyObject builder) {
        def model = {
            "vo-dml:model"("xsi:schemaLocation": "${this.ns} http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/xsd/vo-dml-v1.0.xsd") {
                name(this.name)
                description(this.description)
                title(this.title)
                this.authors.each {
                    author(it)
                }
                version(this.version)
                this.previousVersions.each {
                    previousVersion(it)
                }
                lastModified(this.lastModified)
                this.imports.each {
                    out << it
                }
                this.primitiveTypes.each {
                    out << it
                }
                this.enumerations.each {
                    out << it
                }
                this.dataTypes.each {
                    out << it
                }
                this.objectTypes.each {
                    out << it
                }
                this.packages.each {
                    out << it
                }
            }
        }
        model.delegate = builder
        model()
    }
}
