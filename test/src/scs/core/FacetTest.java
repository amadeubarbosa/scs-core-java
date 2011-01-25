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
  private static ComponentContext context;
  private static String name;
  private static String interfaceName;
  private static Servant servant;
  private static org.omg.CORBA.Object facetReference;

  @BeforeClass
  public static void beforeClass() throws UserException, SCSException {
    ORB orb = ORB.init((String[]) null, null);
    org.omg.CORBA.Object obj = orb.resolve_initial_references("RootPOA");
    POA poa = POAHelper.narrow(obj);
    ComponentId componentId =
      new ComponentId("componente", (byte) 1, (byte) 0, (byte) 0, "java");
    context = new ComponentContext(orb, poa, componentId);
    name = "Facet";
    interfaceName = IMetaInterfaceHelper.id();
    servant = new IMetaInterfaceServant(context);
    facetReference = context.getPOA().servant_to_reference(servant);
  }

  @AfterClass
  public static void afterClass() {
    context = null;
  }

  @Test(expected = IllegalArgumentException.class)
  public void constructFacet() {
    new Facet(null, interfaceName, facetReference, servant);
  }

  @Test(expected = IllegalArgumentException.class)
  public void constructFacet2() {
    new Facet(name, null, facetReference, servant);
  }

  @Test(expected = IllegalArgumentException.class)
  public void constructFacet3() {
    new Facet(name, interfaceName, null, servant);
  }

  @Test(expected = IllegalArgumentException.class)
  public void constructFacet4() {
    new Facet(name, interfaceName, facetReference, null);
  }

  @Test
  public void constructFacet5() {
    new Facet(name, interfaceName, facetReference, servant);
  }

  @Test
  public void getName() {
    Facet facet = new Facet(name, interfaceName, facetReference, servant);
    Assert.assertNotNull(facet.getName());
    Assert.assertEquals(name, facet.getName());
    Assert.assertEquals(facet.getName(), facet.getDescription().name);
  }

  @Test
  public void getInterfaceName() {
    Facet facet = new Facet(name, interfaceName, facetReference, servant);
    Assert.assertNotNull(facet.getInterfaceName());
    Assert.assertEquals(interfaceName, facet.getInterfaceName());
    Assert.assertEquals(facet.getInterfaceName(),
      facet.getDescription().interface_name);
  }

  @Test
  public void getReferece() {
    Facet facet = new Facet(name, interfaceName, facetReference, servant);
    Assert.assertNotNull(facet.getReference());
  }

  @Test
  public void getServant() {
    Facet facet = new Facet(name, interfaceName, facetReference, servant);
    Assert.assertNotNull(facet.getServant());
  }
}
