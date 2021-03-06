package scs.core;

import java.util.Collection;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.omg.CORBA.ORB;
import org.omg.CORBA.UserException;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import scs.core.exception.FacetAlreadyExists;
import scs.core.exception.FacetDoesNotExist;
import scs.core.exception.ReceptacleAlreadyExistsException;
import scs.core.exception.ReceptacleDoesNotExistException;
import scs.core.exception.SCSException;

public final class ComponentContextTest {
  private static ORB orb;
  private static POA poa;
  private static ComponentId componentId;

  @BeforeClass
  public static void beforeClass() throws UserException {
    Properties properties = new Properties();
    properties.put("org.omg.CORBA.ORBClass", "org.jacorb.orb.ORB");
    properties.put("org.omg.CORBA.ORBSingletonClass",
      "org.jacorb.orb.ORBSingleton");
    orb = ORB.init((String[]) null, properties);

    org.omg.CORBA.Object obj = orb.resolve_initial_references("RootPOA");
    poa = POAHelper.narrow(obj);
    poa.the_POAManager().activate();

    Thread thread = new Thread(new Runnable() {
      public void run() {
        orb.run();
      }
    });
    thread.start();

    componentId =
      new ComponentId("componente", (byte) 1, (byte) 0, (byte) 0, "java");

  }

  @AfterClass
  public static void afterClass() {
    orb.shutdown(true);
    orb.destroy();
  }

  @Test
  public void constructContext() throws SCSException {
    new ComponentContext(orb, poa, componentId);
  }

  @Test(expected = IllegalArgumentException.class)
  public void constructContext2() throws SCSException {
    new ComponentContext(null, poa, componentId);
  }

  @Test(expected = IllegalArgumentException.class)
  public void constructContext3() throws SCSException {
    new ComponentContext(orb, null, componentId);
  }

  @Test(expected = IllegalArgumentException.class)
  public void constructContext4() throws SCSException {
    new ComponentContext(orb, poa, null);
  }

  @Test
  public void getORB() throws SCSException {
    ComponentContext component = new ComponentContext(orb, poa, componentId);
    Assert.assertNotNull(component.getORB());
  }

  @Test
  public void getPOA() throws SCSException {
    ComponentContext component = new ComponentContext(orb, poa, componentId);
    Assert.assertNotNull(component.getPOA());
  }

  @Test
  public void getPOA2() throws SCSException, UserException {
    ComponentContext component = new ComponentContext(orb, poa, componentId);
    Assert.assertNotNull(component.getPOA());
    org.omg.CORBA.Object obj =
      component.getORB().resolve_initial_references("RootPOA");
    Assert.assertNotNull(obj);
    POA poa = POAHelper.narrow(obj);
    Assert.assertEquals(poa, component.getPOA());
  }

  @Test
  public void getFacets() throws SCSException {
    ComponentContext component = new ComponentContext(orb, poa, componentId);
    Collection<Facet> facets = component.getFacets();
    Assert.assertNotNull(facets);
    Assert.assertEquals(3, facets.size());
  }

  @Test
  public void getReceptacles() throws SCSException {
    ComponentContext component = new ComponentContext(orb, poa, componentId);
    Collection<Receptacle> receptacles = component.getReceptacles();
    Assert.assertNotNull(receptacles);
    Assert.assertEquals(0, receptacles.size());
  }

  @Test
  public void getComponentId() throws SCSException {
    ComponentContext component = new ComponentContext(orb, poa, componentId);
    Assert.assertNotNull(component.getComponentIdAsString());
    ComponentId otherComponentId = component.getComponentId();
    Assert.assertNotNull(otherComponentId);
    Assert.assertEquals(componentId.name, otherComponentId.name);
    Assert.assertEquals(componentId.major_version,
      otherComponentId.major_version);
    Assert.assertEquals(componentId.minor_version,
      otherComponentId.minor_version);
    Assert.assertEquals(componentId.patch_version,
      otherComponentId.patch_version);
    Assert.assertEquals(componentId.platform_spec,
      otherComponentId.platform_spec);
  }

  @Test(expected = IllegalArgumentException.class)
  public void getFacetByName() throws SCSException {
    ComponentContext component = new ComponentContext(orb, poa, componentId);
    component.getFacetByName(null);
  }

  @Test
  public void getFacetByName2() throws SCSException {
    ComponentContext component = new ComponentContext(orb, poa, componentId);
    Assert.assertNull(component.getFacetByName(""));
  }

  @Test(expected = IllegalArgumentException.class)
  public void getReceptacleByName() throws SCSException {
    ComponentContext component = new ComponentContext(orb, poa, componentId);
    component.getReceptacleByName(null);
  }

  @Test
  public void getReceptacleByName2() throws SCSException {
    ComponentContext component = new ComponentContext(orb, poa, componentId);
    Assert.assertNull(component.getReceptacleByName(""));
  }

  @Test
  public void getIComponent() throws SCSException {
    ComponentContext component = new ComponentContext(orb, poa, componentId);
    IComponent iComponent = component.getIComponent();
    Assert.assertNotNull(iComponent);
    Facet facet =
      component.getFacetByName(ComponentContext.ICOMPONENT_FACET_NAME);
    Assert.assertNotNull(facet);
    FacetDescription description = facet.getDescription();
    Assert.assertEquals(iComponent, description.facet_ref);
    Assert.assertEquals(IComponentHelper.id(), facet.getInterfaceName());
    Assert.assertEquals(IComponentHelper.id(), description.interface_name);
    Assert
      .assertEquals(ComponentContext.ICOMPONENT_FACET_NAME, facet.getName());
    Assert.assertEquals(ComponentContext.ICOMPONENT_FACET_NAME,
      description.name);
  }

  @Test
  public void getIComponent2() throws SCSException {
    ComponentContext component = new ComponentContext(orb, poa, componentId);
    IComponent iComponent = component.getIComponent();
    component.removeFacet(ComponentContext.ICOMPONENT_FACET_NAME);
    iComponent = component.getIComponent();
    Assert.assertNull(iComponent);
  }

  @Test
  public void getIReceptacles() throws SCSException {
    ComponentContext component = new ComponentContext(orb, poa, componentId);
    Facet facet =
      component.getFacetByName(ComponentContext.IRECEPTACLES_FACET_NAME);
    Assert.assertNotNull(facet);
    FacetDescription description = facet.getDescription();
    Assert.assertEquals(IReceptaclesHelper.id(), facet.getInterfaceName());
    Assert.assertEquals(IReceptaclesHelper.id(), description.interface_name);
    Assert.assertEquals(ComponentContext.IRECEPTACLES_FACET_NAME, facet
      .getName());
    Assert.assertEquals(ComponentContext.IRECEPTACLES_FACET_NAME,
      description.name);
  }

  @Test
  public void getIMetaInterface() throws SCSException {
    ComponentContext component = new ComponentContext(orb, poa, componentId);
    Facet facet =
      component.getFacetByName(ComponentContext.IMETAINTERFACE_FACET_NAME);
    Assert.assertNotNull(facet);
    FacetDescription description = facet.getDescription();
    Assert.assertEquals(IMetaInterfaceHelper.id(), facet.getInterfaceName());
    Assert.assertEquals(IMetaInterfaceHelper.id(), description.interface_name);
    Assert.assertEquals(ComponentContext.IMETAINTERFACE_FACET_NAME, facet
      .getName());
    Assert.assertEquals(ComponentContext.IMETAINTERFACE_FACET_NAME,
      description.name);
  }

  @Test
  public void addFacet() throws SCSException {
    String facetName = "nome";
    ComponentContext component = new ComponentContext(orb, poa, componentId);
    component.addFacet(facetName, IComponentHelper.id(), new IComponentServant(
      component));
    Assert.assertNotNull(component.getFacetByName(facetName));
  }

  @Test(expected = FacetAlreadyExists.class)
  public void addFacet2() throws SCSException {
    ComponentContext component = new ComponentContext(orb, poa, componentId);
    component.addFacet(ComponentContext.ICOMPONENT_FACET_NAME, IComponentHelper
      .id(), new IComponentServant(component));
  }

  @Test
  public void updateFacet() throws SCSException {
    ComponentContext component = new ComponentContext(orb, poa, componentId);
    component.updateFacet(ComponentContext.ICOMPONENT_FACET_NAME,
      new IComponentServant(component));
  }

  @Test(expected = FacetDoesNotExist.class)
  public void updateFacet2() throws SCSException {
    ComponentContext component = new ComponentContext(orb, poa, componentId);
    component.updateFacet("nome", new IComponentServant(component));
  }

  @Test
  public void removeFacet() throws SCSException {
    String facetName = "nome";
    ComponentContext component = new ComponentContext(orb, poa, componentId);
    component.addFacet(facetName, IComponentHelper.id(), new IComponentServant(
      component));
    Assert.assertNotNull(component.getFacetByName(facetName));
    component.removeFacet(facetName);
    Assert.assertNull(component.getFacetByName(facetName));
  }

  @Test(expected = FacetDoesNotExist.class)
  public void removeFacet2() throws SCSException {
    String facetName = "nome";
    ComponentContext component = new ComponentContext(orb, poa, componentId);
    component.removeFacet(facetName);
  }

  @Test
  public void addReceptacle() throws SCSException {
    String receptacleName = "nome";
    ComponentContext component = new ComponentContext(orb, poa, componentId);
    component.addReceptacle(receptacleName, IComponentHelper.id(), false);
    Assert.assertNotNull(component.getReceptacleByName(receptacleName));
  }

  @Test(expected = ReceptacleAlreadyExistsException.class)
  public void addReceptacle2() throws SCSException {
    String receptacleName = "nome";
    ComponentContext component = new ComponentContext(orb, poa, componentId);
    component.addReceptacle(receptacleName, IComponentHelper.id(), false);
    component.addReceptacle(receptacleName, IComponentHelper.id(), true);
  }

  @Test
  public void removeReceptacle() throws SCSException {
    String receptacleName = "nome";
    ComponentContext component = new ComponentContext(orb, poa, componentId);
    component.addReceptacle(receptacleName, IComponentHelper.id(), false);
    Assert.assertNotNull(component.getReceptacleByName(receptacleName));
    component.removeReceptacle(receptacleName);
    Assert.assertNull(component.getReceptacleByName(receptacleName));
  }

  @Test(expected = ReceptacleDoesNotExistException.class)
  public void removeReceptacle2() throws SCSException {
    String receptacleName = "nome";
    ComponentContext component = new ComponentContext(orb, poa, componentId);
    component.removeReceptacle(receptacleName);
  }
}
