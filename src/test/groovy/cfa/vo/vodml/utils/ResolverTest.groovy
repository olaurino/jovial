package cfa.vo.vodml.utils

import cfa.vo.vodml.Model
import cfa.vo.vodml.io.VodmlReader
import org.junit.Test


// TODO docs and error handling
class ResolverTest {
    @Test
    public void testIndex() {
        Resolver resolver = Resolver.instance
        def reader = new VodmlReader()
        Model modelSpec = reader.read(getClass().getResource("/DatasetMetadata-1.0.vo-dml.xml").openStream())
        resolver << modelSpec

        assert resolver.resolveType("ds:dataset.Dataset").name == "Dataset"
        assert resolver.resolveType("ds:party.Organization").name == "Organization"
        assert resolver.resolveRole("ds:party.Party.name").name == "name"

        assert resolver.extends("ds:party.Individual", "ds:party.Party")
        assert !resolver.extends("ds:party.Individual", "ds:dataset.Dataset")
        assert resolver.extends("ds:dataset.Publisher", "ds:party.Role")

        assert resolver.resolveAttribute("ds:party.Party", "name") == new VodmlRef("ds:party.Party.name")
        assert resolver.resolveAttribute("ds:party.Organization", "name") == new VodmlRef("ds:party.Party.name")
    }
}
