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

import scs.core.ComponentContext;
import scs.core.builder.XMLComponentBuilder;
import scs.core.exception.SCSException;
import scs.demos.stockmarket.servant.FileExchangePrinterImpl;

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

    // ORB initialization.
    ORB orb = ORB.init(args, orbProps);
    POA poa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
    poa.the_POAManager().activate();

    // "Metadata-only" component creation
    // The API will be responsible for creating the servants and registering
    // them with the POA.
    File is = new File("resources/" + args[0]);
    XMLComponentBuilder xmlBuilder = new XMLComponentBuilder();
    ComponentContext context;
    try {
      context = xmlBuilder.build(orb, poa, is);
    }
    catch (SCSException e) {
      e.getCause().printStackTrace();
      return;
    }

    FileExchangePrinterImpl fp =
      (FileExchangePrinterImpl) context.getFacetByName("FileExchangePrinter")
        .getServant();
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
