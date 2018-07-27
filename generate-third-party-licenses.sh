#!/bin/bash

mvn org.codehaus.mojo:license-maven-plugin:1.13:aggregate-add-third-party     
cp target/generated-sources/license/THIRD-PARTY.txt THIRD-PARTY-JAVA.txt
