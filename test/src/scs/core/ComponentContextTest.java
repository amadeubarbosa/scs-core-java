package scs.core;

import java.util.Collection;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.omg.CORBA.ORB;
import org.omg.CORBA.UserException;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import scs.core.exception.SCSException;

public final class ComponentContextTest {
  private static ORB orb;
  private static POA poa;
  private static ComponentId componentId;

  @BeforeClass
  public static void beforeClass() throws UserException {
    orb = ORB.init((String[]) null, null);
    org.omg.CORBA.Object obj = orb.resolve_initial_references("RootPOA");
    poa = POAHelper.narrow(obj);
    componentId =
      new ComponentId("componente", (byte) 1, (byte) 0, (byte) 0, "java");
  }

  @AfterClass
  public static void afterClass() {
    componentId = null;
    poa.destroy(true, true);
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
    Assert.assertNull(component.getFacetByName(""));
  }

  @Test
  public void getIComponent() throws SCSException {
    ComponentContext component = new ComponentContext(orb, poa, componentId);
    IComponent iComponent = component.getIComponent();
    Assert.assertNotNull(iComponent);
    String iComponentFacetName = "IComponent";
    Facet facet = component.getFacetByName(iComponentFacetName);
    Assert.assertNotNull(facet);
    FacetDescription description = facet.getDescription();
    Assert.assertEquals(iComponent, description.facet_ref);
    Assert.assertEquals(IComponentHelper.id(), facet.getInterfaceName());
    Assert.assertEquals(IComponentHelper.id(), description.interface_name);
    Assert.assertEquals(iComponentFacetName, facet.getName());
    Assert.assertEquals(iComponentFacetName, description.name);
  }

  @Test
  public void getIReceptacles() throws SCSException {
    ComponentContext component = new ComponentContext(orb, poa, componentId);
    String iReceptaclesFacetName = "IReceptacles";
    Facet facet = component.getFacetByName(iReceptaclesFacetName);
    Assert.assertNotNull(facet);
    FacetDescription description = facet.getDescription();
    Assert.assertEquals(IReceptaclesHelper.id(), facet.getInterfaceName());
    Assert.assertEquals(IReceptaclesHelper.id(), description.interface_name);
    Assert.assertEquals(iReceptaclesFacetName, facet.getName());
    Assert.assertEquals(iReceptaclesFacetName, description.name);
  }

  @Test
  public void getIMetaInterface() throws SCSException {
    ComponentContext component = new ComponentContext(orb, poa, componentId);
    String iMetaInterfaceFacetName = "IMetaInterface";
    Facet facet = component.getFacetByName(iMetaInterfaceFacetName);
    Assert.assertNotNull(facet);
    FacetDescription description = facet.getDescription();
    Assert.assertEquals(IMetaInterfaceHelper.id(), facet.getInterfaceName());
    Assert.assertEquals(IMetaInterfaceHelper.id(), description.interface_name);
    Assert.assertEquals(iMetaInterfaceFacetName, facet.getName());
    Assert.assertEquals(iMetaInterfaceFacetName, description.name);
  }
}
