package scs.core;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.omg.CORBA.ORB;
import org.omg.CORBA.UserException;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.Servant;

import scs.core.exception.SCSException;

public final class FacetTest {
  private static ORB orb;
  private static ComponentContext context;
  private static String name;
  private static String interfaceName;
  private static Servant servant;

  @BeforeClass
  public static void beforeClass() throws UserException, SCSException {
    orb = ORB.init((String[]) null, null);
    org.omg.CORBA.Object obj = orb.resolve_initial_references("RootPOA");
    POA poa = POAHelper.narrow(obj);
    poa.the_POAManager().activate();
    new Thread() {
      @Override
      public void run() {
        orb.run();
      }
    }.start();
    ComponentId componentId =
      new ComponentId("componente", (byte) 1, (byte) 0, (byte) 0, "java");
    context = new ComponentContext(orb, poa, componentId);
    name = "Facet";
    interfaceName = IMetaInterfaceHelper.id();
    servant = new IMetaInterfaceServant(context);
  }

  @AfterClass
  public static void afterClass() {
    orb.shutdown(false);
    context = null;
  }

  @Test(expected = IllegalArgumentException.class)
  public void constructFacet() throws SCSException {
    new Facet(context.getPOA(), null, interfaceName, servant);
  }

  @Test(expected = IllegalArgumentException.class)
  public void constructFacet2() throws SCSException {
    new Facet(context.getPOA(), name, null, servant);
  }

  @Test(expected = IllegalArgumentException.class)
  public void constructFacet3() throws SCSException {
    new Facet(null, name, interfaceName, servant);
  }

  @Test(expected = IllegalArgumentException.class)
  public void constructFacet4() throws SCSException {
    new Facet(context.getPOA(), name, interfaceName, null);
  }

  @Test
  public void constructFacet5() throws SCSException {
    new Facet(context.getPOA(), name, interfaceName, servant);
  }

  @Test
  public void getName() throws SCSException {
    Facet facet = new Facet(context.getPOA(), name, interfaceName, servant);
    Assert.assertNotNull(facet.getName());
    Assert.assertEquals(name, facet.getName());
    Assert.assertEquals(facet.getName(), facet.getDescription().name);
  }

  @Test
  public void getInterfaceName() throws SCSException {
    Facet facet = new Facet(context.getPOA(), name, interfaceName, servant);
    Assert.assertNotNull(facet.getInterfaceName());
    Assert.assertEquals(interfaceName, facet.getInterfaceName());
    Assert.assertEquals(facet.getInterfaceName(),
      facet.getDescription().interface_name);
  }

  @Test
  public void getReferece() throws SCSException {
    Facet facet = new Facet(context.getPOA(), name, interfaceName, servant);
    Assert.assertNotNull(facet.getReference());
  }

  @Test
  public void getServant() throws SCSException {
    Facet facet = new Facet(context.getPOA(), name, interfaceName, servant);
    Assert.assertNotNull(facet.getServant());
  }

  @Test(expected = IllegalArgumentException.class)
  public void setServant() throws SCSException {
    Facet facet = new Facet(context.getPOA(), name, interfaceName, servant);
    facet.setServant(null);
  }

  @Test
  public void deactivate() throws SCSException {
    Facet facet = new Facet(context.getPOA(), name, interfaceName, servant);
    facet.deactivate();
  }

  @Test(expected = SCSException.class)
  public void deactivate2() throws SCSException {
    Facet facet = new Facet(context.getPOA(), name, interfaceName, servant);
    facet.deactivate();
    facet.deactivate();
  }
}
