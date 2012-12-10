-------------------------------------------------------------------------------
                     SCS - Software Component System
                    http://www.tecgraf.puc-rio.br/scs
-------------------------------------------------------------------------------

-------------------------------------------------------------------------------
PROGRAMMER LEVEL 1 - Just developing a SCS component
-------------------------------------------------------------------------------
COMPILING:

All code using Maven:
  mvn install

The JAR file generated will be placed on target directory.

In order to execute without the Maven integration, you must:
  1) create a SCS installation directory
  mkdir /tmp/install
  
  2) copy all dependencies into it:
  mvn dependency:copy-dependencies -DincludeScope=runtime \
    -DoutputDirectory=/tmp/install
  cp target/scs-demos-helloworld-1.2.1.1.jar /tmp/install
  
  3) now the RUNNING step could be successfully executed (see bin/rundemo to
  understand how to change the SCS installation directory)

RUNNING:

1 The Java Hello server (saves the CORBA reference in the file: hello.ior):
  ./bin/rundemo scs.demos.helloworld.BasicServerApp

2 The Java Hello client (reads the CORBA reference from the file: hello.ior):
  ./bin/rundemo scs.demos.helloworld.BasicClientApp

