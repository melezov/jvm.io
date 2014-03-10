#!/bin/bash
# Convert source XMLs to JSON
cd source
for f in *.xml
do	
	xml2json $f > ../reference/$f.json
done
cd ../reference
# Convert resulting JSONs to XML again
for g in *.json
do
	json2xml $g > $g.xml
done
