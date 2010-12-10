package scs.demos.stockmarket.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import scs.core.ComponentId;
import scs.core.IComponent;
import scs.core.IComponentHelper;
import scs.core.exception.SCSException;
import scs.demos.stockmarket.servant.StockExchangeImpl;
import scs.demos.stockmarket.servant.StockSellerIComponentImpl;
import scs.demos.stockmarket.servant.StockServerImpl;
import scs.demos.stockmarket.util.StockSellerContext;
import StockMarket.ExchangePrinter;
import StockMarket.ExchangePrinterHelper;
import StockMarket.StockExchange;
import StockMarket.StockExchangeHelper;
import StockMarket.StockServer;
import StockMarket.StockServerHelper;

/**
 * This server code is used to execute a StockSeller component.
 * 
 * @author augusto
 * 
 */
public class StockSellerServer {

  /**
   * @param args
   * @throws NoSuchMethodException
   * @throws InvocationTargetException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws ClassNotFoundException
   * @throws SecurityException
   * @throws IllegalArgumentException
   * @throws BadKind
   * @throws WrongPolicy
   * @throws ServantNotActive
   * @throws InvalidName
   * @throws AdapterInactive
   * @throws IOException
   */
  public static void main(String[] args) throws ServantNotActive, WrongPolicy,
    BadKind, IllegalArgumentException, SecurityException,
    ClassNotFoundException, InstantiationException, IllegalAccessException,
    InvocationTargetException, NoSuchMethodException, InvalidName,
    AdapterInactive, IOException {

    // These properties are used to force JacORB instead of Sun's ORB
    // implementation.
    Properties orbProps = new Properties();
    orbProps.setProperty("org.omg.CORBA.ORBClass", "org.jacorb.orb.ORB");
    orbProps.setProperty("org.omg.CORBA.ORBSingletonClass",
      "org.jacorb.orb.ORBSingleton");
    orbProps.setProperty("OAIAddr", "localhost");

    // ORB initialization.
    ORB orb = ORB.init(args, orbProps);
    POA poa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
    poa.the_POAManager().activate();

    // "Manual" component creation
    // We are responsible for creating the servants
    ComponentId cpId =
      new ComponentId("StockSeller", (byte) 1, (byte) 0, (byte) 0, "java");

    StockSellerContext component;
    try {
      component = new StockSellerContext(orb, poa, cpId);

      component.putFacet(IComponent.class.getSimpleName(), IComponentHelper
        .id(), new StockSellerIComponentImpl(component));
      component.putFacet(StockServer.class.getSimpleName(), StockServerHelper
        .id(), new StockServerImpl(component));
      component.putFacet(StockExchange.class.getSimpleName(),
        StockExchangeHelper.id(), new StockExchangeImpl(component));

      component.putReceptacle(ExchangePrinter.class.getSimpleName(),
        ExchangePrinterHelper.id(), true);

      // Writes the reference of the IComponent facet to a file
      PrintWriter ps =
        new PrintWriter(new FileOutputStream(new File("seller.ior")));
      ps.println(orb.object_to_string(component.getIComponent()));
      ps.close();

      // Blocks the current thread, waiting for calls, until the ORB is 
      // finalized
      orb.run();
    }
    catch (SCSException e) {
      e.getCause().printStackTrace();
    }
  }
}
