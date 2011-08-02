package scs.core;

import java.util.Properties;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.omg.CORBA.ORB;
import org.omg.CORBA.UserException;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import scs.core.exception.SCSException;

public final class IMetaInterfaceTest {
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

  @Test
  public void getFacets() {
    Facet facet = context.getFacetByName("IMetaInterface");
    IMetaInterface meta = IMetaInterfaceHelper.narrow(facet.getReference());
    FacetDescription[] facets = meta.getFacets();
    Assert.assertEquals(3, facets.length);
  }

  @Test
  public void getFacetsByName() throws InvalidName {
    Facet facet = context.getFacetByName("IMetaInterface");
    IMetaInterface meta = IMetaInterfaceHelper.narrow(facet.getReference());
    FacetDescription[] facets =
      meta.getFacetsByName(new String[] { "IComponent", "IReceptacles",
          "IMetaInterface" });
    Assert.assertEquals(3, facets.length);
  }

  @Test(expected = InvalidName.class)
  public void getFacetsByName2() throws InvalidName {
    Facet facet = context.getFacetByName("IMetaInterface");
    IMetaInterface meta = IMetaInterfaceHelper.narrow(facet.getReference());
    meta.getFacetsByName(new String[] { "" });
  }

  @Test
  public void getReceptacles() {
    Facet facet = context.getFacetByName("IMetaInterface");
    IMetaInterface meta = IMetaInterfaceHelper.narrow(facet.getReference());
    ReceptacleDescription[] receptacles = meta.getReceptacles();
    Assert.assertEquals(0, receptacles.length);
  }

  @Test(expected = InvalidName.class)
  public void getReceptacleByName() throws InvalidName {
    Facet facet = context.getFacetByName("IMetaInterface");
    IMetaInterface meta = IMetaInterfaceHelper.narrow(facet.getReference());
    meta.getReceptaclesByName(new String[] { "" });
  }
}
