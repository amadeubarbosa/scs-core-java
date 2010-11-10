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
import scs.core.FacetDescription;
import scs.core.IComponentHelper;
import scs.core.ReceptacleDescription;
import scs.core.servant.ComponentBuilder;
import scs.demos.stockmarket.servant.StockExchangeImpl;
import scs.demos.stockmarket.servant.StockSellerIComponentImpl;
import scs.demos.stockmarket.servant.StockServerImpl;
import scs.demos.stockmarket.util.StockSellerContext;
import StockMarket.ExchangePrinterHelper;
import StockMarket.StockExchangeHelper;
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
    // We are responsible for creating the servants and registering them with
    // the POA
    ComponentId cpId =
      new ComponentId("StockSeller", (byte) 1, (byte) 0, (byte) 0, "java");

    ComponentBuilder builder = new ComponentBuilder(poa, orb);

    StockSellerContext context = new StockSellerContext(builder, cpId);

    FacetDescription[] facetDescs = new FacetDescription[3];

    FacetDescription icDesc =
      new FacetDescription("IComponent", IComponentHelper.id(), poa
        .servant_to_reference(new StockSellerIComponentImpl(context)));
    facetDescs[0] = icDesc;

    FacetDescription ssDesc =
      new FacetDescription("StockServer", StockServerHelper.id(), poa
        .servant_to_reference(new StockServerImpl(context)));
    facetDescs[1] = ssDesc;

    FacetDescription seDesc =
      new FacetDescription("StockExchange", StockExchangeHelper.id(), poa
        .servant_to_reference(new StockExchangeImpl(context)));
    facetDescs[2] = seDesc;

    ReceptacleDescription[] receptDescs = new ReceptacleDescription[1];
    ReceptacleDescription receptDesc =
      new ReceptacleDescription("ExchangePrinter", ExchangePrinterHelper.id(),
        true, null);
    receptDescs[0] = receptDesc;

    builder.newComponent(facetDescs, receptDescs, cpId, context);

    // Writes the reference of the IComponent facet to a file
    PrintWriter ps =
      new PrintWriter(new FileOutputStream(new File("seller.ior")));
    ps.println(orb.object_to_string(context.getIComponent()));
    ps.close();

    // Blocks the current thread, waiting for calls, until the ORB is 
    // finalized
    orb.run();
  }
}
