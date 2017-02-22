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

    resource(id: "table_objects") {

        table(id: "_table1") {

            def sourceNames = ['08120809-0206132', '08115683-0205428', '08115826-0205336']
            def ra = [123.033734, 122.986794, 122.992773]
            def dec = [-2.103671, -2.095231, -2.092676]
            def j = [14.161, 15.860, 16.273]
            def jErr = [0.025, 0.060, 0.096]
            def h = [13.681, 15.103, 15.718]
            def hErr = [0.027, 0.077, 0.112]
            def k = [13.675, 14.847, 15.460]
            def kErr = [0.048, 0.127, 0.212]

            instance(type: "sample:catalog.Source", id: "_source") {
                pk() {
                    column(role: "name", id: "_designation", data: sourceNames)
                }
                instance(role: "position") {
                    column(role: "longitude", id: "_ra", data: ra)
                    column(role: "latitude", id: "_dec", data: dec)
                    reference(role: "frame") {
                        idref("_icrs")
                    }
                }
//            ['J', 'H', 'K'].each { name ->
//                instance(role: "luminosity") {
//                    column(role: "value", id: "_mag$name")
//                    column(role: "error", id: "_err$name")
//                    instance(role: "type", value: "magnitude")
//                    reference(role: "filter") {
//                        idref("_2mass$name")
//                    }
//                }
//            }
                instance(role: "luminosity") {
                    column(role: "value", id: "_magH", data: h)
                    column(role: "error", id: "_errH", data: hErr)
                    instance(role: "type", value: "magnitude")
                    reference(role: "filter") {
                        idref("_2massH")
                    }
                }
                instance(role: "luminosity") {
                    column(role: "value", id: "_magK", data: k)
                    column(role: "error", id: "_errK", data: kErr)
                    instance(role: "type", value: "magnitude")
                    reference(role: "filter") {
                        idref("_2massK")
                    }
                }
                instance(role: "luminosity") {
                    column(role: "error", id: "_errJ", data: jErr)
                    reference(role: "filter") {
                        idref("_2massJ")
                    }
                    column(role: "value", id: "_magJ", data: j)
                    instance(role: "type", value: "magnitude")
                }
                composition(role: "luminosity", ref: "SDSS_MAGS")
            }
        }

        def sourceId = ["08120809-0206132", "08120809-0206132"]
        def mag = [23.2, 23.0]
        def err = [0.04, 0.03]
        def filterId = ["sdss:g", "sdss:r"]

        table(id: "_sdss_mags") {
            instance(id: "SDSS_MAGS", type: "sample:catalog.LuminosityMeasurement") {
                fk(target: "_source") {
                    column(id: "_container", data: sourceId)
                }
                column(role: "value", id: "_mag", data: mag)
                column(role: "error", id: "_eMag", data: err)
                reference(role: "filter") {
                    fk(target: "_SDSS_FILTERS") {
                        column(id: "_filter", data: filterId)
                    }
                }
            }
        }
    }
}