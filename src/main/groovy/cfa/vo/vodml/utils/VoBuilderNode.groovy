package cfa.vo.vodml.utils


interface VoBuilderNode extends Buildable {
    void start(Map attrs)
    void apply()
    void end()
}