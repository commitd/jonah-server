del /q build\*.jar
rmdir /s /q build\plugins
mkdir build
mkdir build\plugins

REM ##########################################
echo "Building Java plugins"
call mvn clean install -DskipTests

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
