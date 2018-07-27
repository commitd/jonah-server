#!/bin/bash

rm build/*.jar
rm -rf build/plugins
mkdir -p build/plugins

##########################################
echo "Building Java plugins"
mvn clean install -DskipTests

##########################################
echo "Moving plugins to build directory"

cp ketos-runner/target/*.jar build/.

cp ketos-common/target/*.jar build/plugins/.
cp ketos-core/target/*.jar build/plugins/.
cp ketos-graphql-baleen/target/*.jar build/plugins/.
cp ketos-data-baleen-mongo/target/*.jar build/plugins/.
cp ketos-data-baleen-elasticsearch/target/*.jar build/plugins/.
cp ketos-feedback/target/*.jar build/plugins/.
cp ketos-doc-cluster/target/*.jar build/plugins/.

##########################################
echo "Done"
