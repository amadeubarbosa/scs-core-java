#!/bin/sh

# change here to upgrade maven version information!
VERSION=1.2-SNAPSHOT

ant jar-core
mvn install:install-file -DgroupId=tecgraf.scs -DartifactId=scs-core -Dversion=${VERSION} -Dpackaging=jar -Dfile=libs/scs.jar
mvn install:install-file -DgroupId=tecgraf.scs -DartifactId=scs-idl-jacorb -Dversion=${VERSION} -Dpackaging=jar -Dfile=libs/scsidl.jar
