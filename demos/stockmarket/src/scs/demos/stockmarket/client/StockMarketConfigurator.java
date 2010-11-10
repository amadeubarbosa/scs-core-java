package scs.demos.stockmarket.client;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.omg.CORBA.ORB;

import scs.core.AlreadyConnected;
import scs.core.ExceededConnectionLimit;
import scs.core.IComponent;
import scs.core.IComponentHelper;
import scs.core.IReceptacles;
import scs.core.IReceptaclesHelper;
import scs.core.InvalidConnection;
import scs.core.InvalidName;
import scs.core.StartupFailed;
import StockMarket.ExchangePrinter;
import StockMarket.ExchangePrinterHelper;

/**
 * This client code is used to connect printers from a StockLogger component to
 * a StockSeller component.
 * 
 * @author augusto
 * 
 */
public class StockMarketConfigurator {

  /**
   * @param args
   * @throws AlreadyConnected
   * @throws ExceededConnectionLimit
   * @throws InvalidName
   * @throws InvalidConnection
   * @throws IOException
   */
  public static void main(String[] args) throws InvalidConnection, InvalidName,
    ExceededConnectionLimit, AlreadyConnected, IOException {

    // These properties are used to force JacORB instead of Sun's ORB
    // implementation.
    Properties orbProps = new Properties();
    orbProps.setProperty("org.omg.CORBA.ORBClass", "org.jacorb.orb.ORB");
    orbProps.setProperty("org.omg.CORBA.ORBSingletonClass",
      "org.jacorb.orb.ORBSingleton");
    orbProps.setProperty("OAIAddr", "localhost");

    // ORB initialization.
    ORB orb = ORB.init(args, orbProps);

    // Reads the StockSeller and StockLogger IORs from files
    BufferedReader reader =
      new BufferedReader(new InputStreamReader(
        new FileInputStream("seller.ior")));
    String iorSeller = reader.readLine();

    BufferedReader reader2 =
      new BufferedReader(new InputStreamReader(
        new FileInputStream("logger.ior")));
    String iorPrinter = reader2.readLine();

    // Creates a StockSeller's IComponent proxy and calls startup.
    // Should receive an error!
    org.omg.CORBA.Object obj = orb.string_to_object(iorSeller);
    IComponent icStockSeller = IComponentHelper.narrow(obj);
    try {
      icStockSeller.startup();
    }
    catch (StartupFailed e) {
      System.out
        .println("Startup threw an exception correctly because there isn't any printer connected to it yet.");
    }

    // Obtains the reference for the StockSeller's IReceptacles facet
    IReceptacles irStockSeller =
      IReceptaclesHelper
        .narrow(icStockSeller.getFacet(IReceptaclesHelper.id()));

    // Obtains the references for ExchangePrinter facets
    org.omg.CORBA.Object obj2 = orb.string_to_object(iorPrinter);
    IComponent icExchangePrinter = IComponentHelper.narrow(obj2);
    ExchangePrinter displayPrinter =
      ExchangePrinterHelper.narrow(icExchangePrinter
        .getFacetByName("DisplayExchangePrinter"));
    ExchangePrinter filePrinter =
      ExchangePrinterHelper.narrow(icExchangePrinter
        .getFacetByName("FileExchangePrinter"));

    if (!displayPrinter._is_a("IDL:StockMarket/ExchangePrinter:1.0")) {
      System.out.println("Display printer is not an ExchangePrinter!!");
    }
    if (!filePrinter._is_a("IDL:StockMarket/ExchangePrinter:1.0")) {
      System.out.println("File printer is not an ExchangePrinter!!");
    }

    // Conects the printers to the StockSeller component
    irStockSeller.connect("ExchangePrinter", displayPrinter);
    irStockSeller.connect("ExchangePrinter", filePrinter);

    // Calls startup again. This time there shouldn't be any errors.
    try {
      icStockSeller.startup();
      icExchangePrinter.startup();
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    System.out.println("Configuration successfully completed.");
  }

}
