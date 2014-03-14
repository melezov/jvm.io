#!/bin/bash
# Convert source XMLs to JSON
cd source
for f in *.json
do	
	json2xml $f > ../reference/$f.xml
done
cd ../reference
# Convert resulting JSONs to XML again
for g in *.xml
do
	xml2json $g > $g.json
done
