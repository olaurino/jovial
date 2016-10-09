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
package cfa.vo.vodml.metamodel

import ca.odell.glazedlists.BasicEventList
import ca.odell.glazedlists.EventList
import cfa.vo.vodml.io.ModelWriter
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
            "vo-dml:model"("xsi:schemaLocation": "${ModelWriter.NS} http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/xsd/vo-dml-v1.0.xsd") {
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
