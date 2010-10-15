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

RUNNING:

1 The Java Hello server (saves the CORBA reference in the file: hello.ior):
  ./bin/rundemo scs.demos.helloworld.BasicServerApp

2 The Java Hello client (reads the CORBA reference from the file: hello.ior):
  ./bin/rundemo scs.demos.helloworld.BasicClientApp

