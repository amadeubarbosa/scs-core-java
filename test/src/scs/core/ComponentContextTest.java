package scs.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
  public void putFacet() throws SCSException {
    String facetName = "nome";
    ComponentContext component = new ComponentContext(orb, poa, componentId);
    component.putFacet(facetName, IComponentHelper.id(), new IComponentServant(
      component));
    Assert.assertNotNull(component.getFacetByName(facetName));
  }

  @Test
  public void removeFacet() throws SCSException {
    String facetName = "nome";
    ComponentContext component = new ComponentContext(orb, poa, componentId);
    component.putFacet(facetName, IComponentHelper.id(), new IComponentServant(
      component));
    Assert.assertNotNull(component.getFacetByName(facetName));
    component.removeFacet(facetName);
    Assert.assertNull(component.getFacetByName("nome"));
  }

  @Test
  public void removeFacet2() throws SCSException {
    String facetName = "nome";
    ComponentContext component = new ComponentContext(orb, poa, componentId);
    component.removeFacet(facetName);
  }

  @Test
  public void putReceptacle() throws SCSException {
    String receptacleName = "nome";
    ComponentContext component = new ComponentContext(orb, poa, componentId);
    component.putReceptacle(receptacleName, IComponentHelper.id(), false);
    Assert.assertNotNull(component.getReceptacleByName(receptacleName));
  }

  @Test
  public void removeReceptacle() throws SCSException {
    String receptacleName = "nome";
    ComponentContext component = new ComponentContext(orb, poa, componentId);
    component.putReceptacle("nome", IComponentHelper.id(), false);
    Assert.assertNotNull(component.getReceptacleByName(receptacleName));
    component.removeReceptacle(receptacleName);
    Assert.assertNull(component.getReceptacleByName(receptacleName));
  }

  @Test
  public void activateComponent() throws SCSException {
    ComponentContext component = new ComponentContext(orb, poa, componentId);
    Map<String, SCSException> errors = component.activateComponent();
    Assert.assertEquals(component.getFacets().size(), errors.size());
  }

  @Test
  public void activateComponent2() throws SCSException {
    ComponentContext component = new ComponentContext(orb, poa, componentId);
    Collection<Facet> facets = component.getFacets();
    Set<String> facetsNames = new HashSet<String>(facets.size());
    for (Facet facet : facets) {
      facetsNames.add(facet.getName());
    }
    for (String facetName : facetsNames) {
      component.removeFacet(facetName);
    }
    Map<String, SCSException> errors = component.activateComponent();
    Assert.assertEquals(0, errors.size());
  }

  @Test
  public void deactivateComponent() throws SCSException {
    ComponentContext component = new ComponentContext(orb, poa, componentId);
    Map<String, SCSException> errors = component.deactivateComponent();
    Assert.assertEquals(0, errors.size());
  }
}
