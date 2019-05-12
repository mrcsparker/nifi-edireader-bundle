# NIFI EDIReader Processor

[Apache NIFI](https://nifi.apache.org) processor that converts
EDI [ASC X12](https://en.wikipedia.org/wiki/ASC_X12) and
[EDIFACT](https://en.wikipedia.org/wiki/EDIFACT)
documents into XML using the
[EDIReader](https://github.com/BerryWorksSoftware/edireader) library.

## Usage

* [Download Apache NIFI](https://nifi.apache.org/download.html)

* Grab EDIReader from github

`nifi-edireader-processor` uses a new version of EDIReader that is not available in a Maven repo.

```bash
> git clone https://github.com/BerryWorksSoftware/edireader.git
> cd edireader
> mvn install
```

* Compile and install `nifi-edireader-processor`

```bash
> cd nifi-edireader-processor
> mvn package
> cp nifi-edireader-nar/target/nifi-edireader-nar-1.9.2.nar /NIFI_INSTALL/lib/
```

## License

The code for this project is licensed under the Apache 2 License.

The code for EDIReader is licensed under the GNU GPL v3.

When used with the GPLv3 version of EDIReader the license for project falls under the GNU GPL v3 license.



