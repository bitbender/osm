# osm

An extractor for grabbing values from osm pbf dumps. The software simply iterates every entity in the dump and writes every entity belonging to certain groups (http://wiki.openstreetmap.org/wiki/Map_Features) into json dumps. The currently dumped groups are:

* place
* office
* shop
* craft
* amenity
* leisure
* building
* club
* sport
* tourism
* emergency
* historic
* highway

Usage

Launch the tool with the source file as the first argument and the target folder as an optional second argument.
