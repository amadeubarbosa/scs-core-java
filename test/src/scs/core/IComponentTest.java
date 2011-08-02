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

public final class IComponentTest {
  private static final String COMPONENT_NAME = "componente";
  private static final byte COMPONENT_MAJOR_VERSION = 1;
  private static final byte COMPONENT_MINOR_VERSION = 0;
  private static final byte COMPONENT_PATCH_VERSION = 0;
  private static final String COMPONENT_PLATFORM_SPEC = "java";
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
      new ComponentId(COMPONENT_NAME, COMPONENT_MAJOR_VERSION,
        COMPONENT_MINOR_VERSION, COMPONENT_PATCH_VERSION,
        COMPONENT_PLATFORM_SPEC);
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
  public void getComponentId() {
    Facet facet = context.getFacetByName("IComponent");
    IComponent component = IComponentHelper.narrow(facet.getReference());
    ComponentId id = component.getComponentId();
    Assert.assertEquals(COMPONENT_NAME, id.name);
    Assert.assertEquals(COMPONENT_MAJOR_VERSION, id.major_version);
    Assert.assertEquals(COMPONENT_MINOR_VERSION, id.minor_version);
    Assert.assertEquals(COMPONENT_PATCH_VERSION, id.patch_version);
    Assert.assertEquals(COMPONENT_PLATFORM_SPEC, id.platform_spec);
  }

  @Test
  public void getFacet() {
    Facet facet = context.getFacetByName("IComponent");
    IComponent component = IComponentHelper.narrow(facet.getReference());
    Assert.assertNotNull(component.getFacet(IComponentHelper.id()));
    Assert.assertNotNull(component.getFacet(IReceptaclesHelper.id()));
    Assert.assertNotNull(component.getFacet(IMetaInterfaceHelper.id()));
    Assert.assertNull(component.getFacet(""));
  }

  @Test
  public void getFacetByName() {
    Facet facet = context.getFacetByName("IComponent");
    IComponent component = IComponentHelper.narrow(facet.getReference());
    Assert.assertNotNull(component.getFacetByName("IComponent"));
    Assert.assertNotNull(component.getFacetByName("IReceptacles"));
    Assert.assertNotNull(component.getFacetByName("IMetaInterface"));
    Assert.assertNull(component.getFacetByName(""));
  }
}
