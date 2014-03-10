
TODO: Update, directory layout changed for source file type

Round trip tests xml->json and vice versa.


Directories:
    - source - source XML and JSON files
    - converted - converted files and roundtrip files (conversions by our jvm.io converter)
    - reference - reference converted files and roundtrip files  (conversions by Json.NET's converter)

E.g. for _source.xml_:
    - source
        - source.xml
    - converted
        - source.xml.json
        - source.xml.json.xml
    - reference
        - source.xml.json
        - source.xml.json.xml

Likewise, for _source.json_
    - source
        - source.json
    - converted
        - source.json.xml
        - source.json.xml.json
    - reference
        - source.json.xml
        - source.json.xml.json

The script generate\_reference\_conversions.sh generates neccessary fresh conversions to xml and json using Json.Net (includes round-tripping).
