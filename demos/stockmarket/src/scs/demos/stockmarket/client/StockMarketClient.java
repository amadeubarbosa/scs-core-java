package scs.demos.stockmarket.client;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.omg.CORBA.ORB;

import scs.core.IComponent;
import scs.core.IComponentHelper;
import StockMarket.StockExchange;
import StockMarket.StockExchangeHelper;
import StockMarket.StockServer;
import StockMarket.StockServerHelper;

/**
 * This client code is used to retrieve a list of stocks from a StockSeller
 * component and then buy one of each stock.
 * 
 * @author augusto
 */
public class StockMarketClient {

  /**
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {

    // These properties are used to force JacORB instead of Sun's ORB
    // implementation.
    Properties orbProps = new Properties();
    orbProps.setProperty("org.omg.CORBA.ORBClass", "org.jacorb.orb.ORB");
    orbProps.setProperty("org.omg.CORBA.ORBSingletonClass",
      "org.jacorb.orb.ORBSingleton");
    orbProps.setProperty("OAIAddr", "localhost");

    // ORB initialization.
    ORB orb = ORB.init(args, orbProps);

    // Reads the StockSeller IOR from a file
    BufferedReader reader =
      new BufferedReader(new InputStreamReader(
        new FileInputStream("seller.ior")));
    String iorSeller = reader.readLine();

    // Creates proxies for a StockSeller component
    org.omg.CORBA.Object obj = orb.string_to_object(iorSeller);
    IComponent icStockSeller = IComponentHelper.narrow(obj);

    StockServer server =
      StockServerHelper.narrow(icStockSeller.getFacetByName("StockServer"));

    StockExchange exchange =
      StockExchangeHelper.narrow(icStockSeller.getFacetByName("StockExchange"));

    String[] stocks = server.getStockSymbols();

    // Buys one of each stock
    for (int i = 0; i < stocks.length; i++) {
      if (exchange.buyStock(stocks[i])) {
        System.out.println("Stock " + stocks[i] + " bought.");
      }
      else {
        System.out.println("Stock " + stocks[i]
          + " is not available for purchase.");
      }
    }
  }

}
