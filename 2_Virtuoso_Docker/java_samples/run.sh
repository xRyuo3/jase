#!/bin/bash

### Example 1 - Execute SPARQL query on Virtuoso from Java ###
javac -cp "./lib/virtjdbc4.jar" ExecQuery.java
java -cp "./lib/virtjdbc4.jar" ExecQuery.java
rm *.class

### Example 2 - Run Owl reasoner (provided by Jena) from Java ###
javac -cp .:lib/* OwlReasoner.java
java -cp .:lib/* OwlReasoner.java
rm *.class

### Example 3 - Insert local Turtle file in Virtuoso from Java ###
javac -cp .:lib/* VirtuosoInsertTriples.java
java -cp .:lib/* VirtuosoInsertTriples.java
rm *.class
