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
  cp target/scs-demos-stockmarket-1.3.0-SNAPSHOT.jar /tmp/install
  
  3) now the RUNNING step could be successfully executed (see bin/rundemo to
  understand how to change the SCS installation directory)

RUNNING:

1 The Java StockSeller server (saves the CORBA reference in the file: seller.ior):
  ./bin/rundemo scs.demos.stockmarket.server.StockSellerServer

2 The Java StockLogger server (saves the CORBA reference in the file: logger.ior):
  ./bin/rundemo scs.demos.stockmarket.server.StockLoggerServer

3 The Java StockMarket configurator (connects both servers reading the ior files):
  ./bin/rundemo scs.demos.stockmarket.client.StockMarketConfigurator

4 The Java StockMarket client:
  ./bin/rundemo scs.demos.stockmarket.client.StockMarketClient

The output should be seen both at the screen and the file logger.txt.

