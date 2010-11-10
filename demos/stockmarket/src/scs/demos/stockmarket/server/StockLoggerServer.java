package scs.demos.stockmarket.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import scs.core.servant.ComponentBuilder;
import scs.core.servant.ComponentContext;
import scs.core.servant.ExtendedFacetDescription;
import scs.demos.stockmarket.servant.DisplayExchangePrinterImpl;
import scs.demos.stockmarket.servant.FileExchangePrinterImpl;
import StockMarket.ExchangePrinterHelper;

/**
 * This server code is used to execute a StockLogger component.
 * 
 * @author augusto
 * 
 */
public class StockLoggerServer {

  /**
   * @param args
   * @throws InvalidName
   * @throws AdapterInactive
   * @throws WrongPolicy
   * @throws ServantNotActive
   * @throws NoSuchMethodException
   * @throws InvocationTargetException
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws BadKind
   * @throws SecurityException
   * @throws IllegalArgumentException
   * @throws FileNotFoundException
   */
  public static void main(String[] args) throws InvalidName, AdapterInactive,
    ServantNotActive, WrongPolicy, IllegalArgumentException, SecurityException,
    BadKind, InstantiationException, IllegalAccessException,
    ClassNotFoundException, InvocationTargetException, NoSuchMethodException,
    FileNotFoundException {

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

    // "Metadata-only" component creation
    // The API will be responsible for creating the servants and registering
    // them with the POA.
    // TODO: This metadata could be read from a properties file instead of
    //       being hard-coded.
    ComponentId cpId =
      new ComponentId("StockSeller", (byte) 1, (byte) 0, (byte) 0, "java");

    ComponentBuilder builder = new ComponentBuilder(poa, orb);

    ExtendedFacetDescription[] extFacetDescs = new ExtendedFacetDescription[2];

    ExtendedFacetDescription depDesc =
      new ExtendedFacetDescription("DisplayExchangePrinter",
        ExchangePrinterHelper.id(), DisplayExchangePrinterImpl.class
          .getCanonicalName());
    extFacetDescs[0] = depDesc;

    ExtendedFacetDescription fepDesc =
      new ExtendedFacetDescription("FileExchangePrinter", ExchangePrinterHelper
        .id(), FileExchangePrinterImpl.class.getCanonicalName());
    extFacetDescs[1] = fepDesc;

    ComponentContext context = builder.newComponent(extFacetDescs, cpId);
    FileExchangePrinterImpl fp =
      (FileExchangePrinterImpl) context.getFacets().get("FileExchangePrinter");
    File f = new File("logger.txt");
    fp.setFile(f);

    // Writes the reference of the IComponent facet to a file
    PrintWriter ps =
      new PrintWriter(new FileOutputStream(new File("logger.ior")));
    ps.println(orb.object_to_string(context.getIComponent()));
    ps.close();

    // Blocks the current thread, waiting for calls, until the ORB is 
    // finalized
    orb.run();
  }
}
