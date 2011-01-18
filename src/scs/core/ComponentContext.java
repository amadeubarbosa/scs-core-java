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

public class ComponentContext {
  private ORB orb;
  private POA poa;
  private ComponentId componentId;
  private Map<String, Facet> facets;
  private Map<String, Receptacle> receptacles;

  /**
   * Contador para gerar o ID da conexao (por instancia de componente)
   */
  private int currentConnectionId = 0;

  /**
   * Limite de conexões por componente
   */
  public final int CONNECTION_LIMIT = 100;

  /**
   * Method that serves as a factory for the component.
   * 
   * @return A new component
   * @throws SCSException
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
        //TODO: logar como warn que _get_component não foi definida.
        /*
         * System.err.println("_get_component nao foi definida para a classe " +
         * name + "!");
         */

      }
    }
    catch (NoSuchMethodException e) {
      //TODO: logar como warn que _get_component não foi definida.
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
        poa
          .deactivate_object(poa.reference_to_id(facet.getDescription().facet_ref));
      }
      catch (UserException e) {
        throw new SCSException(e);
      }
    }
  }

  public ComponentId getComponentId() {
    return componentId;
  }

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
        //TODO: logar que uma faceta foi substituída
      }
      else {
        //TODO: logar que uma faceta foi adicionada
      }
    }
    catch (UserException e) {
      throw new SCSException(e);
    }
  }

  public void putReceptacle(String name, String interface_name,
    boolean is_multiplex) {
    Receptacle receptacle =
      new Receptacle(this, name, interface_name, is_multiplex);
    if (receptacles.put(name, receptacle) != null) {
      //TODO: logar que um receptaculo foi substituido e todas as suas conexões, perdidas
    }
    else {
      //TODO: logar que um receptaculo foi adicionado
    }
  }

  public void removeFacet(String name) throws SCSException {
    Facet facet = facets.get(name);
    deactivateFacet(facet);
    facets.remove(name);
    //TODO: logar que uma faceta foi removida
  }

  public void removeReceptacle(String name) {
    if (receptacles.containsKey(name)) {
      Receptacle receptacle = receptacles.remove(name);
      if (receptacle != null) {
        //TODO: logar que um receptaculo foi removido e todas as suas conexões, perdidas
      }
    }
  }

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
   * TODO: descricao nao esta mais correta!!
   * 
   * Desativa todas as facetas do componente. As referências facet_ref continuam
   * não-nulas após a chamada, para manter o acesso ao objeto Java. O usuário
   * fica responsável por reativar as facetas quando considerar apropriado.
   * 
   * @param context Instância do componente.
   * 
   * @return Mapa contendo os nomes das facetas(chaves) e as mensagens de
   *         erro(valores) das facetas que não puderam ser desativadas.
   */
  public Map<String, SCSException> deactivateComponent() {
    Map<String, SCSException> errMsgs = new HashMap<String, SCSException>();
    for (Facet facet : facets.values()) {
      try {
        poa
          .deactivate_object(poa.reference_to_id(facet.getDescription().facet_ref));
      }
      catch (UserException e) {
        //TODO: logar erro ao desativar faceta como warn
        errMsgs.put(facet.getName(), new SCSException(e));
      }
    }
    return errMsgs;
  }

  public Collection<Facet> getFacets() {
    return Collections.unmodifiableCollection(facets.values());
  }

  public Facet getFacetByName(String name) {
    if (name == null) {
      throw new IllegalArgumentException("The facet's name can't be null");
    }
    return facets.get(name);
  }

  public Collection<Receptacle> getReceptacles() {
    return Collections.unmodifiableCollection(receptacles.values());
  }

  public Receptacle getReceptacleByName(String name) {
    return receptacles.get(name);
  }

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
