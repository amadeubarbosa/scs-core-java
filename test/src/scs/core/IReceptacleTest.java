package scs.core;

import java.util.List;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.omg.CORBA.ORB;
import org.omg.CORBA.UserException;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import scs.core.exception.SCSException;

public final class IReceptacleTest {
  private static ComponentContext context;
  private static String name;
  private static String interfaceName;
  private static boolean multiplex;

  @BeforeClass
  public static void beforeClass() throws UserException, SCSException {
    ORB orb = ORB.init((String[]) null, null);
    org.omg.CORBA.Object obj = orb.resolve_initial_references("RootPOA");
    POA poa = POAHelper.narrow(obj);
    ComponentId componentId =
      new ComponentId("componente", (byte) 1, (byte) 0, (byte) 0, "java");
    context = new ComponentContext(orb, poa, componentId);
    name = "Receptáculo";
    interfaceName = IReceptaclesHelper.id();
    multiplex = false;
  }

  @AfterClass
  public static void afterClass() {
    context = null;
  }

  @Test(expected = IllegalArgumentException.class)
  public void constructReceptacle() {
    new Receptacle(null, name, interfaceName, multiplex);
  }

  @Test(expected = IllegalArgumentException.class)
  public void constructReceptacle2() {
    new Receptacle(context, null, interfaceName, multiplex);
  }

  @Test(expected = IllegalArgumentException.class)
  public void constructReceptacle3() {
    new Receptacle(context, name, null, multiplex);
  }

  @Test
  public void constructReceptacle4() {
    new Receptacle(context, name, interfaceName, multiplex);
  }

  @Test
  public void getName() {
    Receptacle receptacle =
      new Receptacle(context, name, interfaceName, multiplex);
    Assert.assertNotNull(receptacle.getName());
    Assert.assertEquals(name, receptacle.getName());
  }

  @Test
  public void getInterfaceName() {
    Receptacle receptacle =
      new Receptacle(context, name, interfaceName, multiplex);
    Assert.assertNotNull(receptacle.getInterfaceName());
    Assert.assertEquals(interfaceName, receptacle.getInterfaceName());
  }

  @Test
  public void isMultiplex() {
    Receptacle receptacle =
      new Receptacle(context, name, interfaceName, multiplex);
    Assert.assertEquals(multiplex, receptacle.isMultiplex());
  }

  @Test
  public void getConnections() {
    Receptacle receptacle =
      new Receptacle(context, name, interfaceName, multiplex);
    List<ConnectionDescription> connections = receptacle.getConnections();
    Assert.assertNotNull(connections);
    Assert.assertEquals(0, connections.size());
  }

  @Test
  public void getConnectionSize() {
    Receptacle receptacle =
      new Receptacle(context, name, interfaceName, multiplex);
    Assert.assertEquals(0, receptacle.getConnectionsSize());
  }

  @Test
  public void getDescription() {
    Receptacle receptacle =
      new Receptacle(context, name, interfaceName, multiplex);
    ReceptacleDescription description = receptacle.getDescription();
    Assert.assertNotNull(description.name);
    Assert.assertEquals(name, description.name);
    Assert.assertNotNull(description.interface_name);
    Assert.assertEquals(interfaceName, description.interface_name);
    Assert.assertEquals(multiplex, description.is_multiplex);
    ConnectionDescription[] connections = description.connections;
    Assert.assertNotNull(connections);
    Assert.assertEquals(0, connections.length);
  }

  @Test(expected = IllegalArgumentException.class)
  public void addConnection() throws UserException {
    Receptacle receptacle =
      new Receptacle(context, name, interfaceName, multiplex);
    receptacle.addConnection(null);
  }

  @Test
  public void addConnection2() throws UserException {
    Receptacle receptacle =
      new Receptacle(context, name, interfaceName, multiplex);
    POA poa = context.getPOA();
    byte[] id =
      poa.servant_to_id(new IReceptaclesServant(context));
    org.omg.CORBA.Object obj = poa.id_to_reference(id);
    receptacle.addConnection(obj);
    Assert.assertEquals(1, receptacle.getConnectionsSize());
    poa.deactivate_object(id);
  }
}
