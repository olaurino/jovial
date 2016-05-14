[![Build Status](https://travis-ci.org/olaurino/jovial.svg?branch=master)](https://travis-ci.org/olaurino/jovial)
[![Build status](https://ci.appveyor.com/api/projects/status/f2e9k6may71a9w3h/branch/master?svg=true)](https://ci.appveyor.com/project/olaurino/jovial/branch/master)
[![Coverage Status](https://coveralls.io/repos/github/olaurino/jovial/badge.svg?branch=master)](https://coveralls.io/github/olaurino/jovial?branch=master)

# Jovial
### A modeling toolset for the Virtual Observatory

Jovial is a library that helps building interoperable data models according to the International Virtual Observatory
standards, in particular the Virtual Observatory Data Modeling Language (VODML).

## Features

  * A Java/Groovy API for reading and writing standard data model descriptors.
  * A streamlined Domain Specific Language for defining data models and serialize them as standard descriptors.
  * A Domain Specific Language for expressing instances of data model types.
  
The library tries to abstract and simplify the definition of models and their instances as much as possible.

## Examples
The following Groovy code:

```groovy
import cfa.vo.vodml.metamodel.Model
import cfa.vo.vodml.io.VodmlWriter
import cfa.vo.vodml.io.ModelBuilder

Model model = new ModelBuilder().model("ds") {
            title("Dataset Metadata")
            description("Generic, high-level metadata associated with an IVOA Dataset.")
            lastModified("2016-04-20T16:44:59.239-04:00")
            author("John Doe")
            author("Jane Doe")
            include("ivoa", version:"1.0", url: "https://some/url")
            pack("dataset") {
                enumeration("DataProductType") {
                    literal("CUBE", description: "Data Cube")
                    literal("IMAGE", description: "Image")
                    literal("PHOTOMETRY", description: "Photometry")
                    literal("SPECTRUM", description: "Spectrum")
                    literal("TIMESERIES", description: "Time Series")
                    literal("SED", description: "Spectral Energy Distribution")
                    literal("VISIBILITY", description: "Visibility")
                    literal("EVENT", description: "Event List")
                    literal("CATALOG", description: "Catalog")
                }
                enumeration("CreationType") {
                    literal("ARCHIVAL", description: "Archival")
                    literal("CUTOUT", description: "Cutout")
                    literal("FILTERED", description: "Filtered")
                    literal("MOSAIC", description: "Mosaic")
                    literal("SPECTRAL_EXTRACTION", description: "Spectral Extraction")
                    literal("CATALOG_EXTRACTION", description: "Catalog Extraction")
                }
                enumeration("RightsType") {
                    literal("PUBLIC", description: "Public Access")
                    literal("PROPRIETARY", description: "Proprietary Access")
                    literal("SECURE", description: "Secure Access")
                }
                dataType("Collection") {
                    attribute("name", dataType: ivoa.string)
                }
                dataType("Contributor", parent: ds.party.Role) {
                    attribute("acknowledgement", dataType: ivoa.string)
                }
                dataType("Creator", parent: ds.party.Role)
                objectType("DataID") {
                    attribute("title", dataType: ivoa.string, multiplicity: "0..1")
                }
            }
            pack("party") {
                objectType("Organization", parent: ds.party.Party) {
                    attribute("address", dataType: ivoa.string)
                    attribute("phone", dataType: ivoa.string)
                    attribute("email", dataType: ivoa.string)
                    attribute("logo", dataType: ivoa.anyURI)
                }
            }
        }

new VodmlWriter().write(model, System.out)
```

produces the following XML document:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<vo-dml:model xmlns:vo-dml="http://www.ivoa.net/xml/VODML/v1.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.ivoa.net/xml/VODML/v1.0 http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/xsd/vo-dml-v1.0.xsd">
  <name>ds</name>
  <description>Generic, high-level metadata associated with an IVOA Dataset.</description>
  <title>Dataset Metadata</title>
  <author>John Doe</author>
  <author>Jane Doe</author>
  <version>1.0</version>
  <lastModified>2016-04-20T16:44:59.239-04:00</lastModified>
  <import>
    <name>ivoa</name>
    <version>1.0</version>
    <url>https://some/url</url>
    <documentationURL/>
  </import>
  <package>
    <vodml-id>dataset</vodml-id>
    <name>dataset</name>
    <description/>
    <enumeration>
      <vodml-id>dataset.DataProductType</vodml-id>
      <name>DataProductType</name>
      <description/>
      <literal>
        <vodml-id>dataset.DataProductType.CUBE</vodml-id>
        <name>CUBE</name>
        <description>Data Cube</description>
      </literal>
      <literal>
        <vodml-id>dataset.DataProductType.IMAGE</vodml-id>
        <name>IMAGE</name>
        <description>Image</description>
      </literal>
      <literal>
        <vodml-id>dataset.DataProductType.PHOTOMETRY</vodml-id>
        <name>PHOTOMETRY</name>
        <description>Photometry</description>
      </literal>
      <literal>
        <vodml-id>dataset.DataProductType.SPECTRUM</vodml-id>
        <name>SPECTRUM</name>
        <description>Spectrum</description>
      </literal>
      <literal>
        <vodml-id>dataset.DataProductType.TIMESERIES</vodml-id>
        <name>TIMESERIES</name>
        <description>Time Series</description>
      </literal>
      <literal>
        <vodml-id>dataset.DataProductType.SED</vodml-id>
        <name>SED</name>
        <description>Spectral Energy Distribution</description>
      </literal>
      <literal>
        <vodml-id>dataset.DataProductType.VISIBILITY</vodml-id>
        <name>VISIBILITY</name>
        <description>Visibility</description>
      </literal>
      <literal>
        <vodml-id>dataset.DataProductType.EVENT</vodml-id>
        <name>EVENT</name>
        <description>Event List</description>
      </literal>
      <literal>
        <vodml-id>dataset.DataProductType.CATALOG</vodml-id>
        <name>CATALOG</name>
        <description>Catalog</description>
      </literal>
    </enumeration>
    <enumeration>
      <vodml-id>dataset.CreationType</vodml-id>
      <name>CreationType</name>
      <description/>
      <literal>
        <vodml-id>dataset.CreationType.ARCHIVAL</vodml-id>
        <name>ARCHIVAL</name>
        <description>Archival</description>
      </literal>
      <literal>
        <vodml-id>dataset.CreationType.CUTOUT</vodml-id>
        <name>CUTOUT</name>
        <description>Cutout</description>
      </literal>
      <literal>
        <vodml-id>dataset.CreationType.FILTERED</vodml-id>
        <name>FILTERED</name>
        <description>Filtered</description>
      </literal>
      <literal>
        <vodml-id>dataset.CreationType.MOSAIC</vodml-id>
        <name>MOSAIC</name>
        <description>Mosaic</description>
      </literal>
      <literal>
        <vodml-id>dataset.CreationType.SPECTRAL_EXTRACTION</vodml-id>
        <name>SPECTRAL_EXTRACTION</name>
        <description>Spectral Extraction</description>
      </literal>
      <literal>
        <vodml-id>dataset.CreationType.CATALOG_EXTRACTION</vodml-id>
        <name>CATALOG_EXTRACTION</name>
        <description>Catalog Extraction</description>
      </literal>
    </enumeration>
    <enumeration>
      <vodml-id>dataset.RightsType</vodml-id>
      <name>RightsType</name>
      <description/>
      <literal>
        <vodml-id>dataset.RightsType.PUBLIC</vodml-id>
        <name>PUBLIC</name>
        <description>Public Access</description>
      </literal>
      <literal>
        <vodml-id>dataset.RightsType.PROPRIETARY</vodml-id>
        <name>PROPRIETARY</name>
        <description>Proprietary Access</description>
      </literal>
      <literal>
        <vodml-id>dataset.RightsType.SECURE</vodml-id>
        <name>SECURE</name>
        <description>Secure Access</description>
      </literal>
    </enumeration>
    <dataType>
      <vodml-id>dataset.Collection</vodml-id>
      <name>Collection</name>
      <description/>
      <attribute>
        <vodml-id>dataset.Collection.name</vodml-id>
        <name>name</name>
        <description/>
        <datatype>
          <vodml-ref>ivoa:string</vodml-ref>
        </datatype>
        <multiplicity>
          <minOccurs>1</minOccurs>
          <maxOccurs>1</maxOccurs>
        </multiplicity>
      </attribute>
    </dataType>
    <dataType>
      <vodml-id>dataset.Contributor</vodml-id>
      <name>Contributor</name>
      <description/>
      <extends>
        <vodml-ref>ds:party.Role</vodml-ref>
      </extends>
      <attribute>
        <vodml-id>dataset.Contributor.acknowledgement</vodml-id>
        <name>acknowledgement</name>
        <description/>
        <datatype>
          <vodml-ref>ivoa:string</vodml-ref>
        </datatype>
        <multiplicity>
          <minOccurs>1</minOccurs>
          <maxOccurs>1</maxOccurs>
        </multiplicity>
      </attribute>
    </dataType>
    <dataType>
      <vodml-id>dataset.Creator</vodml-id>
      <name>Creator</name>
      <description/>
      <extends>
        <vodml-ref>ds:party.Role</vodml-ref>
      </extends>
    </dataType>
    <objectType>
      <vodml-id>dataset.DataID</vodml-id>
      <name>DataID</name>
      <description/>
      <attribute>
        <vodml-id>dataset.DataID.title</vodml-id>
        <name>title</name>
        <description/>
        <datatype>
          <vodml-ref>ivoa:string</vodml-ref>
        </datatype>
        <multiplicity>
          <minOccurs>0</minOccurs>
          <maxOccurs>1</maxOccurs>
        </multiplicity>
      </attribute>
    </objectType>
  </package>
  <package>
    <vodml-id>party</vodml-id>
    <name>party</name>
    <description/>
    <objectType>
      <vodml-id>party.Organization</vodml-id>
      <name>Organization</name>
      <description/>
      <extends>
        <vodml-ref>ds:party.Party</vodml-ref>
      </extends>
      <attribute>
        <vodml-id>party.Organization.address</vodml-id>
        <name>address</name>
        <description/>
        <datatype>
          <vodml-ref>ivoa:string</vodml-ref>
        </datatype>
        <multiplicity>
          <minOccurs>1</minOccurs>
          <maxOccurs>1</maxOccurs>
        </multiplicity>
      </attribute>
      <attribute>
        <vodml-id>party.Organization.phone</vodml-id>
        <name>phone</name>
        <description/>
        <datatype>
          <vodml-ref>ivoa:string</vodml-ref>
        </datatype>
        <multiplicity>
          <minOccurs>1</minOccurs>
          <maxOccurs>1</maxOccurs>
        </multiplicity>
      </attribute>
      <attribute>
        <vodml-id>party.Organization.email</vodml-id>
        <name>email</name>
        <description/>
        <datatype>
          <vodml-ref>ivoa:string</vodml-ref>
        </datatype>
        <multiplicity>
          <minOccurs>1</minOccurs>
          <maxOccurs>1</maxOccurs>
        </multiplicity>
      </attribute>
      <attribute>
        <vodml-id>party.Organization.logo</vodml-id>
        <name>logo</name>
        <description/>
        <datatype>
          <vodml-ref>ivoa:anyURI</vodml-ref>
        </datatype>
        <multiplicity>
          <minOccurs>1</minOccurs>
          <maxOccurs>1</maxOccurs>
        </multiplicity>
      </attribute>
    </objectType>
  </package>
</vo-dml:model>
```