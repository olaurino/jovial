package cfa.vo.vodml.utils

import groovy.transform.Canonical


@Canonical
class VodmlRef {
    String prefix
    String reference

    public VodmlRef(String prefix, String reference) {
        this.prefix = prefix
        this.reference = reference
    }

    public VodmlRef(String ref) {
        List tokens = ref.split(":") as List // Lists return null if index is out of bounds
        prefix = tokens[1] ? tokens[0] : null // No ':' means no prefix
        reference = tokens[1] ?: tokens[0]
    }

    public VodmlRef(String prefix, VodmlRef ref) {
        if (ref.prefix) {
            throw new IllegalStateException("Setting prefix on vodmlref with prefix")
        }
        this.prefix = prefix
        this.reference = ref.reference
    }

    public VodmlRef(VodmlRef ref) {
        this.prefix = ref.prefix
        this.reference = ref.reference
    }

    public VodmlRef append(String newPart) {
        return new VodmlRef(prefix, "$reference.$newPart")
    }

    @Override
    String toString() {
        def start = prefix ? "$prefix:" : ""
        return "$start$reference"
    }
}
