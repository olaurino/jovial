package cfa.vo.vodml.utils


interface VoBuilderNode extends Buildable {
    void init(Map attrs)
    void finish()
}