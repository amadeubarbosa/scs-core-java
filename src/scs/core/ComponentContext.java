package scs.core;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.omg.CORBA.ORB;
import org.omg.CORBA.UserException;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;

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
   * it's possible to replace them via the putFacet method. Other facets and
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
    putFacet(IComponent.class.getSimpleName(), IComponentHelper.id(),
      new IComponentServant(this));
    putFacet(IReceptacles.class.getSimpleName(), IReceptaclesHelper.id(),
      new IReceptaclesServant(this));
    putFacet(IMetaInterface.class.getSimpleName(), IMetaInterfaceHelper.id(),
      new IMetaInterfaceServant(this));
  }

  private static void checkForGetComponentMethod(Servant facet) {
    try {
      Method getComponentMethod =
        facet.getClass().getMethod("_get_component", (Class<?>[]) null);
      if (getComponentMethod == null) {
        //TODO: logar como warn que _get_component n�o foi definida.
        /*
         * System.err.println("_get_component nao foi definida para a classe " +
         * name + "!");
         */

      }
    }
    catch (NoSuchMethodException e) {
      //TODO: logar como warn que _get_component n�o foi definida.
      /*
       * System.err.println("_get_component nao foi definida para a classe " +
       * name + "!");
       */
    }
    catch (SecurityException e) {
      //TODO: logar erro.
    }
  }

  private void deactivateFacet(Facet facet) throws SCSException {
    if (facet != null) {
      try {
        poa.deactivate_object(poa
          .reference_to_id(facet.getDescription().facet_ref));
      }
      catch (UserException e) {
        throw new SCSException(e);
      }
    }
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
   * @param interface_name The facet's IDL interface.
   * @param servant The facet implementation, not yet activated within the POA.
   * @throws SCSException If an UserException is catched.
   */
  public void putFacet(String name, String interface_name, Servant servant)
    throws SCSException {
    checkForGetComponentMethod(servant);
    try {
      Facet facet =
        new Facet(name, interface_name, poa.servant_to_reference(servant),
          servant);
      Facet existent = facets.put(name, facet);
      if (existent != null) {
        deactivateFacet(existent);
        //TODO: logar que uma faceta foi substitu�da
      }
      else {
        //TODO: logar que uma faceta foi adicionada
      }
    }
    catch (UserException e) {
      throw new SCSException(e);
    }
  }

  /**
   * Adds a new receptacle to the component instance.
   * 
   * If the receptacle name already exists, the old receptacle will be replaced.
   * 
   * @param name The receptacle's name. This acts as the receptacle identifier
   *        within the component.
   * @param interface_name The receptacle's IDL interface.
   * @param is_multiplex True if the receptacle accepts more than one
   *        connection, false otherwise.
   */
  public void putReceptacle(String name, String interface_name,
    boolean is_multiplex) {
    Receptacle receptacle =
      new Receptacle(this, name, interface_name, is_multiplex);
    if (receptacles.put(name, receptacle) != null) {
      //TODO: logar que um receptaculo foi substituido e todas as suas conex�es, perdidas
    }
    else {
      //TODO: logar que um receptaculo foi adicionado
    }
  }

  /**
   * Removes a facet from the component. The facet is deactivated within the
   * component's POA before being removed.
   * 
   * @param name The name of the facet to be removed.
   * @throws SCSException If an UserException is catched.
   */
  public void removeFacet(String name) throws SCSException {
    Facet facet = facets.get(name);
    deactivateFacet(facet);
    facets.remove(name);
    //TODO: logar que uma faceta foi removida
  }

  /**
   * Removes a receptacle from the component.
   * 
   * @param name The name of the receptacle to be removed.
   */
  public void removeReceptacle(String name) {
    if (receptacles.containsKey(name)) {
      Receptacle receptacle = receptacles.remove(name);
      if (receptacle != null) {
        //TODO: logar que um receptaculo foi removido e todas as suas conex�es, perdidas
      }
    }
  }

  /**
   * Activates all of the component's facets.
   * 
   * @return A map indicating the facets that could not be activated. The map
   *         uses the facet name as an identifier and the catched exception as
   *         the value.
   */
  public Map<String, SCSException> activateComponent() {
    Map<String, SCSException> errMsgs = new HashMap<String, SCSException>();
    for (Facet facet : facets.values()) {
      try {
        this.poa.activate_object(facet.getServant());
      }
      catch (UserException e) {
        //TODO: logar erro ao ativar faceta como warn
        errMsgs.put(facet.getName(), new SCSException(e));
      }
    }
    return errMsgs;
  }

  /**
   * Deactivates all of the component's facets within the POA. The facet_ref
   * references (from the FacetDescription metadata) remain non-null after the
   * call, to maintain access to the Java object.
   * 
   * The user is responsible for reactivating the facets when deemed
   * appropriate.
   * 
   * @return A map indicating the facets that could not be deactivated. The map
   *         uses the facet name as an identifier and the catched exception as
   *         the value.
   */
  public Map<String, SCSException> deactivateComponent() {
    Map<String, SCSException> errMsgs = new HashMap<String, SCSException>();
    for (Facet facet : facets.values()) {
      try {
        poa.deactivate_object(poa
          .reference_to_id(facet.getDescription().facet_ref));
      }
      catch (UserException e) {
        //TODO: logar erro ao desativar faceta como warn
        errMsgs.put(facet.getName(), new SCSException(e));
      }
    }
    return errMsgs;
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
    return receptacles.get(name);
  }

  /**
   * Provides a direct reference to the IComponent facet.
   * 
   * @return The IComponent facet.
   */
  public IComponent getIComponent() {
    Facet facet = facets.get(IComponent.class.getSimpleName());
    if (facet != null) {
      return IComponentHelper.narrow(facet.getDescription().facet_ref);
    }
    return null;
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
