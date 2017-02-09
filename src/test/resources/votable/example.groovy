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
package votable

import cfa.vo.vodml.io.VodmlReader

def location = { path ->
    VodmlReader.getResource("/${path}")
}

def ivoaLocation = location('ivoa.vo-dml.xml')
def ivoaRemoteLocation = new URL("http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/ivoa/IVOA.vo-dml.xml")
def filterLocation = new URL("http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/sample/filter/Filter.vo-dml.xml")
def sampleLocation = new URL("http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/sample/sample/Sample.vo-dml.xml")

def reader = new VodmlReader()

ivoaSpec = reader.read(ivoaLocation.openStream())
filterSpec = reader.read(filterLocation.openStream())
sampleSpec = reader.read(sampleLocation.openStream())

votable {
    model(spec: ivoaSpec, vodmlURL: ivoaRemoteLocation)
    model(spec: filterSpec, vodmlURL: filterLocation, identifier: "ivo://ivoa.org/dm/sample/Filter/1.9")
    model(spec: sampleSpec, vodmlURL: sampleLocation)
    instance(type: "sample:catalog.SkyCoordinateFrame", id: "_icrs") {
        instance(role: "name", value: "ICRS")
    }
    ["2mass:H", "2mass:J", "2mass:K"].each { name ->
        def id = "_${name.replace(':', '')}"
        instance(type: "filter:PhotometryFilter", id: id, pk: id) {
            instance(role: "name", value: name)
        }
    }
}