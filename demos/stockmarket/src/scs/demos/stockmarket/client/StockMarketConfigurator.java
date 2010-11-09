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
    // As propriedades que informam o uso do JacORB como ORB.
    Properties orbProps = new Properties();
    orbProps.setProperty("org.omg.CORBA.ORBClass", "org.jacorb.orb.ORB");
    orbProps.setProperty("org.omg.CORBA.ORBSingletonClass",
      "org.jacorb.orb.ORBSingleton");
    orbProps.setProperty("OAIAddr", "localhost");

    // Inicializa o ORB.
    ORB orb = ORB.init(args, orbProps);

    // Lê o IOR do arquivo cujo nome é passado como parâmetro
    BufferedReader reader =
      new BufferedReader(new InputStreamReader(
        new FileInputStream("seller.ior")));
    String iorSeller = reader.readLine();

    BufferedReader reader2 =
      new BufferedReader(new InputStreamReader(
        new FileInputStream("logger.ior")));
    String iorPrinter = reader2.readLine();

    // Obtém a referência para IComponent do StockSeller e chama startup
    // Deve obter erro!
    org.omg.CORBA.Object obj = orb.string_to_object(iorSeller);
    IComponent icStockSeller = IComponentHelper.narrow(obj);
    try {
      icStockSeller.startup();
    }
    catch (StartupFailed e) {
      System.out
        .println("Startup lançou exceção corretamente pois não há nenhuma impressora conectada ainda.");
    }

    // Obtém a referência para IReceptacles do StockSeller
    IReceptacles irStockSeller =
      IReceptaclesHelper
        .narrow(icStockSeller.getFacet(IReceptaclesHelper.id()));

    // Obtém a referência para as facetas do ExchangePrinter
    org.omg.CORBA.Object obj2 = orb.string_to_object(iorPrinter);
    IComponent icExchangePrinter = IComponentHelper.narrow(obj2);
    ExchangePrinter displayPrinter =
      ExchangePrinterHelper.narrow(icExchangePrinter
        .getFacetByName("DisplayExchangePrinter"));
    ExchangePrinter filePrinter =
      ExchangePrinterHelper.narrow(icExchangePrinter
        .getFacetByName("FileExchangePrinter"));

    if (!displayPrinter._is_a("IDL:StockMarket/ExchangePrinter:1.0")) {
      System.out.println("Display printer com erro!!");
    }
    if (!filePrinter._is_a("IDL:StockMarket/ExchangePrinter:1.0")) {
      System.out.println("File printer com erro!!");
    }
    // Conecta
    irStockSeller.connect("ExchangePrinter", displayPrinter);
    irStockSeller.connect("ExchangePrinter", filePrinter);

    // Chama novamente startup. Dessa vez não deve haver erros.
    try {
      icStockSeller.startup();
      icExchangePrinter.startup();
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    System.out.println("Configuração completada com sucesso.");
  }

}
