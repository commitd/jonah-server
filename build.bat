del build\*.jar
rmdir /s build\plugins
mkdir build
mkdir build\plugins

##########################################
echo "Building Java plugins"
mvn clean install -DskipTests

REM ##########################################
echo "Moving plugins to build directory"

copy ketos-runner\target\*.jar build\.

copy ketos-common\target\*.jar build\plugins\.
copy ketos-core\target\*.jar build\plugins\.
copy ketos-graphql-baleen\target\*.jar build\plugins\.
copy ketos-data-baleen-mongo\target\*.jar build\plugins\.
copy ketos-data-baleen-elasticsearch\target\*.jar build\plugins\.
copy ketos-feedback\target\*.jar build\plugins\.
copy ketos-doc-cluster\target\*.jar build\plugins\.

REM ##########################################
echo "Done"
