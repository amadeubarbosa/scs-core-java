package scs.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;

import scs.core.exception.FacetAlreadyExists;
import scs.core.exception.FacetDoesNotExist;
import scs.core.exception.ReceptacleAlreadyExistsException;
import scs.core.exception.ReceptacleDoesNotExistException;
import scs.core.exception.SCSException;

/**
 * This class is the local representation of a SCS component. Its concept
 * doesn't exist in the CORBA environment. As such, an instance of this class is
 * considered a local instance of a component and gathers all of its facets,
 * receptacles and meta-data. It also holds references to the ORB and POA used
 * to create and activate its facets.
 * 
 * Specifically in Java, this class is also responsible for generating
 * connection id's for receptacles.
 * 
 */
public class ComponentContext {
  public static final String ICOMPONENT_FACET_NAME = "IComponent";
  public static final String IRECEPTACLES_FACET_NAME = "IReceptacles";
  public static final String IMETAINTERFACE_FACET_NAME = "IMetaInterface";

  private ORB orb;
  private POA poa;
  private ComponentId componentId;
  private Map<String, Facet> facets;
  private Map<String, Receptacle> receptacles;

  /**
   * Counter used to generate connection id's. Connection id's are valid per
   * component, not per receptacle.
   */
  private int currentConnectionId = 0;

  /**
   * Limit of concurrent connections, per component. This'll probably be removed
   * in the next version!
   */
  public final int CONNECTION_LIMIT = 100;

  /**
   * Primary constructor. The returned component instance will always have the
   * three basic facets (IComponent, IReceptacles, IMetaInterface) instantiated.
   * If the user wishes to use his own implementation of one of these facets,
   * it's possible to replace them via the addFacet method. Other facets and
   * receptacles can also be added.
   * 
   * The returned instance of this class is considered a new SCS component
   * instance.
   * 
   * @param orb The ORB to be used when creating this component instance's
   *        facets.
   * @param poa The POA to register this component instance's facets.
   * @param id The type of this component.
   * 
   * @throws SCSException If any error occurs. The exception shall contain
   *         another, more specific exception.
   */
  public ComponentContext(ORB orb, POA poa, ComponentId id) throws SCSException {
    if (orb == null) {
      throw new IllegalArgumentException("The ORB can't be null");
    }
    if (poa == null) {
      throw new IllegalArgumentException("The POA can't be null");
    }
    if (id == null) {
      throw new IllegalArgumentException("The component's id can't be null");
    }

    this.orb = orb;
    this.poa = poa;
    componentId = id;

    facets = new HashMap<String, Facet>();
    receptacles = new HashMap<String, Receptacle>();

    addBasicFacets();
  }

  private void addBasicFacets() throws SCSException {
    addFacet(IComponent.class.getSimpleName(), IComponentHelper.id(),
      new IComponentServant(this));
    addFacet(IReceptacles.class.getSimpleName(), IReceptaclesHelper.id(),
      new IReceptaclesServant(this));
    addFacet(IMetaInterface.class.getSimpleName(), IMetaInterfaceHelper.id(),
      new IMetaInterfaceServant(this));
  }

  /**
   * Provides the ComponentId of this component instance. ComponentId's aren't
   * instance identifiers; they specify a component's name, version and platform
   * specification.
   * 
   * @return The component's ComponentId
   */
  public ComponentId getComponentId() {
    return componentId;
  }

  /**
   * Adds a new facet to the component instance. This method activates the facet
   * with the POA associated to the component. Also, it checks for the existence
   * of the _get_component() method. If it's not implemented, a warning will be
   * logged.
   * 
   * If the facet name already exists, the old facet will be replaced and
   * deactivated within the component's POA.
   * 
   * @param name The facet's name. This acts as the facet identifier within the
   *        component.
   * @param interfaceName The facet's IDL interface.
   * @param servant The facet implementation, not yet activated within the POA.
   * @throws SCSException If an UserException is catched.
   */
  public void addFacet(String name, String interfaceName, Servant servant)
    throws SCSException {
    Facet facet = this.facets.get(name);
    if (facet != null) {
      throw new FacetAlreadyExists(name);
    }
    facet = new Facet(this.poa, name, interfaceName, servant);
    facets.put(name, facet);
  }

  public void updateFacet(String name, Servant servant) throws SCSException {
    Facet facet = this.facets.get(name);
    if (facet == null) {
      throw new FacetDoesNotExist(name);
    }
    facet.setServant(servant);
  }

  /**
   * Adds a new receptacle to the component instance.
   * 
   * If the receptacle name already exists, the old receptacle will be replaced.
   * 
   * @param name The receptacle's name. This acts as the receptacle identifier
   *        within the component.
   * @param interfaceName The receptacle's IDL interface.
   * @param isMultiplex True if the receptacle accepts more than one connection,
   *        false otherwise.
   * @throws ReceptacleAlreadyExistsException
   */
  public void addReceptacle(String name, String interfaceName,
    boolean isMultiplex) throws ReceptacleAlreadyExistsException {
    Receptacle receptacle = this.receptacles.get(name);
    if (receptacle != null) {
      throw new ReceptacleAlreadyExistsException(name);
    }
    receptacle = new Receptacle(this, name, interfaceName, isMultiplex);
    receptacles.put(name, receptacle);
    //TODO: logar que um receptaculo foi adicionado
  }

  /**
   * Removes a facet from the component. The facet is deactivated within the
   * component's POA before being removed.
   * 
   * @param name The name of the facet to be removed.
   * @throws SCSException If an UserException is catched.
   */
  public void removeFacet(String name) throws SCSException {
    Facet facet = this.facets.remove(name);
    if (facet == null) {
      throw new FacetDoesNotExist(name);
    }
    facet.deactivate();
    //TODO: logar que uma faceta foi removida
  }

  /**
   * Removes a receptacle from the component.
   * 
   * @param name The name of the receptacle to be removed.
   * @throws ReceptacleDoesNotExistException
   */
  public void removeReceptacle(String name)
    throws ReceptacleDoesNotExistException {
    Receptacle receptacle = receptacles.remove(name);
    if (receptacle == null) {
      throw new ReceptacleDoesNotExistException(name);
    }
    //TODO: logar que um receptaculo foi removido e todas as suas conexões, perdidas
  }

  /**
   * Provides metadata about the component's facets.
   * 
   * @return An unmodifiable collection with the facet metadata.
   */
  public Collection<Facet> getFacets() {
    return Collections.unmodifiableCollection(facets.values());
  }

  /**
   * Provides metadata about a specific facet.
   * 
   * @param name The name of the facet.
   * 
   * @return The facet metadata.
   */
  public Facet getFacetByName(String name) {
    if (name == null) {
      throw new IllegalArgumentException("The facet's name can't be null");
    }
    return facets.get(name);
  }

  /**
   * Provides metadata about the component's receptacles.
   * 
   * @return An unmodifiable collection with the receptacle metadata.
   */
  public Collection<Receptacle> getReceptacles() {
    return Collections.unmodifiableCollection(receptacles.values());
  }

  /**
   * Provides metadata about a specific receptacle.
   * 
   * @param name The name of the receptacle.
   * 
   * @return The receptacle metadata.
   */
  public Receptacle getReceptacleByName(String name) {
    if (name == null) {
      throw new IllegalArgumentException("The receptacle's name can't be null");
    }
    return receptacles.get(name);
  }

  /**
   * Provides a direct reference to the IComponent facet.
   * 
   * @return The IComponent facet.
   */
  public IComponent getIComponent() {
    Facet facet = facets.get(IComponent.class.getSimpleName());
    if (facet == null) {
      return null;
    }
    return IComponentHelper.narrow(facet.getDescription().facet_ref);
  }

  int generateConnectionId() {
    return currentConnectionId++;
  }

  /**
   * Returns a stringified version of the component's id, concatenating its name
   * with the version number and its platform spec.
   * 
   * @return The stringified component's id.
   */
  public String getComponentIdAsString() {
    StringBuilder builder = new StringBuilder();
    builder.append(componentId.name);
    builder.append(componentId.major_version);
    builder.append(componentId.minor_version);
    builder.append(componentId.patch_version);
    builder.append(componentId.platform_spec);
    return builder.toString();
  }

  /**
   * Returns the POA.
   * 
   * @return The POA.
   */
  public POA getPOA() {
    return poa;
  }

  /**
   * Returns the ORB.
   * 
   * @return The ORB.
   */
  public ORB getORB() {
    return orb;
  }
}
