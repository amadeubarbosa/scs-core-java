package scs.core;

import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.omg.CORBA.ORB;
import org.omg.CORBA.UserException;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import scs.core.exception.SCSException;

public final class IReceptaclesTest {
  private static ComponentContext context;

  @BeforeClass
  public static void beforeClass() throws UserException, SCSException {
    Properties properties = new Properties();
    properties.put("org.omg.CORBA.ORBClass", "org.jacorb.orb.ORB");
    properties.put("org.omg.CORBA.ORBSingletonClass",
      "org.jacorb.orb.ORBSingleton");
    ORB orb = ORB.init((String[]) null, properties);

    org.omg.CORBA.Object obj = orb.resolve_initial_references("RootPOA");
    POA poa = POAHelper.narrow(obj);
    poa.the_POAManager().activate();

    ComponentId componentId =
      new ComponentId("componente", (byte) 1, (byte) 0, (byte) 0, "java");
    context = new ComponentContext(orb, poa, componentId);

    Thread thread = new Thread(new Runnable() {
      public void run() {
        context.getORB().run();
      }
    });
    thread.start();
  }

  @AfterClass
  public static void afterClass() {
    ORB orb = context.getORB();
    orb.shutdown(true);
    orb.destroy();
  }

  @Test(expected = InvalidName.class)
  public void getConnections() throws InvalidName {
    Facet facet = context.getFacetByName("IReceptacles");
    IReceptacles receptacles = IReceptaclesHelper.narrow(facet.getReference());
    receptacles.getConnections("");
  }

  @Test(expected = InvalidConnection.class)
  public void disconnect() throws InvalidName, InvalidConnection, NoConnection {
    Facet facet = context.getFacetByName("IReceptacles");
    IReceptacles receptacles = IReceptaclesHelper.narrow(facet.getReference());
    receptacles.disconnect(0);
  }

  @Test(expected = NoConnection.class)
  public void disconnect2() throws InvalidName, InvalidConnection, NoConnection {
    Facet facet = context.getFacetByName("IReceptacles");
    IReceptacles receptacles = IReceptaclesHelper.narrow(facet.getReference());
    receptacles.disconnect(1);
  }
}
