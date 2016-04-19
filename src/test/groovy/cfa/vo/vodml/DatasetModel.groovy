package cfa.vo.vodml

import cfa.vo.vodml.io.XmlWriter

ObjectType role = new ObjectType(
        name: "Role",
        vodmlid: "party.Role",
        references: [
                new Reference(
                        name: "party",
                        vodmlid: "party.Role.party",
                        dataType: new ElementRef (
                                vodmlref: "dataset:party.Party"
                        )
                )
        ]
)

ObjectType party = new ObjectType(
        name: "Party",
        vodmlid: "party.Party",
        attributes: [
                new Attribute(
                        name: "name",
                        vodmlid: "party.Party.name",
                        dataType: new ElementRef(vodmlref: "ivoa:string")
                )
        ]
)

ObjectType organization = new ObjectType(
        name: "Organization",
        vodmlid: "party.Organization",
        extends_: new ElementRef ( vodmlref: "dataset:party.Party" ),
        attributes: [
                new Attribute(
                        name: "address",
                        vodmlid: "party.Organization.address",
                        dataType: new ElementRef(vodmlref: "ivoa:string")
                ),
                new Attribute(
                        name: "phone",
                        vodmlid: "party.Organization.phone",
                        dataType: new ElementRef(vodmlref: "ivoa:string")
                ),
                new Attribute(
                        name: "email",
                        vodmlid: "party.Organization.email",
                        dataType: new ElementRef(vodmlref: "ivoa:string")
                ),
                new Attribute(
                        name: "logo",
                        vodmlid: "party.Organization.logo",
                        dataType: new ElementRef(vodmlref: "ivoa:anyURI")
                )
        ]
)

ObjectType individual = new ObjectType(
        name: "Individual",
        description: "",
        vodmlid: "party.Individual",
        extends_: new ElementRef ( vodmlref: "dataset:party.Party" ),
        attributes: [
            new Attribute(
                    name: "email",
                    vodmlid: "party.Individual.email",
                    dataType: new ElementRef(vodmlref: "ivoa:string")
            ),
            new Attribute(
                    name: "address",
                    vodmlid: "party.Individual.address",
                    dataType: new ElementRef(vodmlref: "ivoa:string")
            ),
            new Attribute(
                    name: "phone",
                    vodmlid: "party.Individual.phone",
                    dataType: new ElementRef(vodmlref: "ivoa:string")
            )
        ]
)

ModelImport ivoa = new ModelImport(
        name: "ivoa",
        version: "1.0",
        url: new URL("http://some/url"),
        documentationURL: new URL("http://someother/url)")
)

Package partyPackage = new Package(
        name: "party",
        vodmlid: "party",
        objectTypes: [ role, party, organization, individual ]
)

Package datasetPackage = new Package(
        name: "dataset",
        vodmlid: "dataset",
)

def datasetModel = new Model(
        name: "dataset",
        title: "dataset",
        description: "Generic, high-level metadata associated with an IVOA Dataset.",
        imports: [ ivoa ],
        authors: [ "Jane Doe", "John Doe" ],
        packages: [ datasetPackage, partyPackage ]
)

def writer = new XmlWriter()
ByteArrayOutputStream os = new ByteArrayOutputStream();
writer.write(datasetModel, os);
String out = os.toString("UTF-8");
System.out.println(out)
