package cfa.vo.vodml

import org.codehaus.groovy.GroovyException


class VodmlException extends GroovyException {
    def VodmlException(String msg, Throwable cause) {
        super(msg, cause)
    }
}
