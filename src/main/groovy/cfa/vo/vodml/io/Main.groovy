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
package cfa.vo.vodml.io

import cfa.vo.vodml.io.instance.VotableWriter

public class Main {
    public static void main(String[] args) {
        def cli = new CliBuilder(usage: "jovial -[mih] input-file")

        cli.with {
            h longOpt: 'help', 'Show usage information'
            m longOpt: 'model', 'convert a model. This is the default'
            i longOpt: 'instance', 'convert an instance'
        }

        def options = cli.parse(args)

        if (!options || options.h || !options.arguments().size()) {
            cli.usage()
            return
        }

        def writer
        def model
        def filename = options.arguments().get(0)

        if (options.i) {
            def modelString = new File(filename).text
            def builder = new VoTableBuilder()
            def binding = new Binding(votable: builder.&script)
            def shell = new GroovyShell(Main.class.classLoader, binding)
            model = shell.evaluate modelString
            writer = new VotableWriter()
        } else {
            def modelString = new File(filename).text
            def builder = new ModelBuilder()
            def binding = new Binding(model: builder.&script)
            def shell = new GroovyShell(Main.class.classLoader, binding)
            model = shell.evaluate modelString
            writer = new ModelWriter()
        }

        writer.write(model, System.out)
    }
}
