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
 * @author augusto
 * 
 */
public class StockMarketClient {

  /**
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    // As propriedades que informam o uso do JacORB como ORB.
    Properties orbProps = new Properties();
    orbProps.setProperty("org.omg.CORBA.ORBClass", "org.jacorb.orb.ORB");
    orbProps.setProperty("org.omg.CORBA.ORBSingletonClass",
      "org.jacorb.orb.ORBSingleton");
    orbProps.setProperty("OAIAddr", "localhost");

    // Inicializa o ORB.
    ORB orb = ORB.init(args, orbProps);

    // L� o IOR do arquivo cujo nome � passado como par�metro
    BufferedReader reader =
      new BufferedReader(new InputStreamReader(
        new FileInputStream("seller.ior")));
    String iorSeller = reader.readLine();

    // Obt�m as refer�ncias do StockSeller
    org.omg.CORBA.Object obj = orb.string_to_object(iorSeller);
    IComponent icStockSeller = IComponentHelper.narrow(obj);

    StockServer server =
      StockServerHelper.narrow(icStockSeller.getFacetByName("StockServer"));

    StockExchange exchange =
      StockExchangeHelper.narrow(icStockSeller.getFacetByName("StockExchange"));

    String[] stocks = server.getStockSymbols();

    // compra uma a��o de cada
    for (int i = 0; i < stocks.length; i++) {
      if (exchange.buyStock(stocks[i])) {
        System.out.println("A��o " + stocks[i] + " comprada.");
      }
      else {
        System.out.println("A��o " + stocks[i]
          + " n�o est� dispon�vel para compra.");
      }
    }
  }

}
