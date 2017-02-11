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

def remote = "http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models"

def ivoaLocation = location('ivoa.vo-dml.xml')
def ivoaRemoteLocation = new URL("$remote/ivoa/IVOA.vo-dml.xml")
def filterLocation = new URL("$remote/sample/filter/Filter.vo-dml.xml")
def sampleLocation = location("votable/Sample.vo-dml.xml")

def reader = new VodmlReader()

ivoaSpec = reader.read(ivoaLocation.openStream())
filterSpec = reader.read(filterLocation.openStream())
sampleSpec = reader.read(sampleLocation.openStream())

votable {
    model(spec: ivoaSpec, vodmlURL: ivoaRemoteLocation)
    model(spec: filterSpec, vodmlURL: filterLocation, identifier: "ivo://ivoa.org/dm/sample/Filter/1.9")
    model(spec: sampleSpec, vodmlURL: "$remote/sample/sample/Sample.vo-dml.xml")

    instance(type: "sample:catalog.SkyCoordinateFrame", id: "_icrs") {
        instance(role: "name", value: "ICRS")
    }

    ["2mass:H", "2mass:J", "2mass:K"].each { name ->
        def id = "_${name.replace(':', '')}"
        instance(type: "filter:PhotometryFilter", id: id) {
            pk(value: id)
            instance(role: "name", value: name)
        }
    }

    globals(id: "_SDSS_FILTERS") {
        ["sdss:g", "sdss:r", "sdss:u"].each { name ->
            instance(type: "filter:PhotometryFilter") {
                pk(value: name)
                instance(role: "name", value: name)
            }
        }
    }

    table(ref: "_table1") {
        instance(type:"sample:catalog.Source", id:"_source") {
            pk() {
                column(role: "name", ref:"_designation")
            }
            instance(role: "position") {
                column(role: "longitude", ref:"_ra")
                column(role: "latitude", ref:"_dec")
                reference(role: "frame") {
                    idref("_icrs")
                }
            }
//            ['H', 'K', 'J'].each { name ->
//                instance(role: "luminosity") {
//                    column(role: "value", ref: "_mag$name")
//                    column(role: "error", ref: "_err$name")
//                    instance(role: "type", value: "magnitude")
//                    reference(role: "filter") {
//                        idref("_2mass$name")
//                    }
//                }
//            }
            instance(role: "luminosity") {
                column(role: "value", ref: "_magH")
                column(role: "error", ref: "_errH")
                instance(role: "type", value: "magnitude")
                reference(role: "filter") {
                    idref("_2massH")
                }
            }
            instance(role: "luminosity") {
                column(role: "value", ref: "_magK")
                column(role: "error", ref: "_errK")
                instance(role: "type", value: "magnitude")
                reference(role: "filter") {
                    idref("_2massK")
                }
            }
            instance(role: "luminosity") {
                column(role: "error", ref: "_errJ")
                reference(role: "filter") {
                    idref("_2massJ")
                }
                column(role: "value", ref: "_magJ")
                instance(role: "type", value: "magnitude")
            }
            external(role: "luminosity", ref:"SDSS_MAGS")
        }
    }

    table(ref: "_sdss_mags") {
        instance(id: "SDSS_MAGS", type: "sample:catalog.LuminosityMeasurement") {
            fk(target: "_source") {
                column(ref: "_container")
            }
            column(role: "value", ref: "_mag")
            column(role: "error", ref: "_eMag")
            reference(role: "filter") {
                fk(target: "_SDSS_FILTERS") {
                    column(ref: "_filter")
                }
            }
        }
    }
}