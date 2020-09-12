#!/usr/bin/env sh
# via  https://github.com/pedestal/pedestal/blob/master/guides/documentation/service-war-deployment.md
set -e
rm -rf target/war
lein pom
mvn dependency:copy-dependencies -DoutputDirectory=target/war/WEB-INF/lib -DincludeScope=runtime
mkdir -p target/war/WEB-INF/classes
cp -R src/* config/* target/war/WEB-INF/classes

# Optionally, you may want to include your static resources in your WAR
# cp -R src/* config/* resources/* target/war/WEB-INF/classes

cp web.xml target/war/WEB-INF

jar cvf target/hello-world-custom.war -C target/war WEB-INF
