#!/usr/bin/env bash
mvn package -DskipTests

export JOVIAL_DIR=${PREFIX}/opt/jovial
export JOVIAL_JAR=${JOVIAL_DIR}/jovial.jar
export JOVIAL_BIN=${PREFIX}/bin/jovial
export JOVIAL_WIN_BIN=${PREFIX}/bin/jovial.bat

mkdir -p ${JOVIAL_DIR}
cp target/jovial*dependencies*.jar ${JOVIAL_JAR}

cat << EOF >> ${JOVIAL_BIN}
#!/bin/bash

java -jar ${JOVIAL_JAR} "\$@"

EOF

cat << EOF >> ${JOVIAL_WIN_BIN}

java -jar ${JOVIAL_JAR} %*

EOF

chmod ug+x ${JOVIAL_BIN}

