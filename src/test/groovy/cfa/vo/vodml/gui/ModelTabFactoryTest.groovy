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

import spock.lang.Specification


class ModelTabFactoryTest extends Specification {
    private FactoryBuilderSupport fbs = Stub(FactoryBuilderSupport)
    private PresentationModel model = new PresentationModel()
    private ModelTabFactory factory = new ModelTabFactory()

    def "Invoke with value only"(){
        when: "factory method is invoked with value only"
            def tab = factory.newInstance(fbs, "name", model, null)

        then: "tab is an instance of ModelTab with correct model"
            assert tab instanceof ModelTab
            assert tab.model == model
    }

    def "Invoke with value and attributes"() {
        when: "factory method is invoked with value and attributes"
            factory.newInstance(fbs, "name", model, [attr: "whatever"])

        then: "exception is thrown"
            def ex = thrown(InstantiationException)
            ex.message == "No attributes allowed in building element. Only a value accepted"
    }

    def "Invoke with null value"() {
        when: "factory method is invoked with null value"
            factory.newInstance(fbs, "name", null, null)

        then: "exception is thrown"
            def ex = thrown(InstantiationException)
            ex.message == "Please provide a PresentationModel value, none was provided"
    }
}
