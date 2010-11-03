package scs.core.servant;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import scs.core.ComponentId;
import scs.core.FacetDescription;
import scs.core.IComponentHelper;
import scs.core.IMetaInterfaceHelper;
import scs.core.IReceptaclesHelper;
import scs.core.ReceptacleDescription;

/**
 * Classe utilit�ria para cria��o de componentes e instancia��o de suas portas
 * (facetas e recept�culos). � poss�vel reproduzir o mesmo comportamento sem o
 * uso dessa classe, caso seja necess�rio.
 * 
 * @author Tecgraf
 * 
 */
public class ComponentBuilder {
  private POA poa;
  private ORB orb;

  public ComponentBuilder(POA poa, ORB orb) {
    this.poa = poa;
    this.orb = orb;
  }

  private ComponentContext createContext(ComponentId id)
    throws IllegalArgumentException {
    if (id == null)
      throw new IllegalArgumentException("ComponentId null!");

    return new ComponentContextImpl(this, id);
  }

  private void testForGetComponent(Class<?> c, String name) {
    try {
      c.getMethod("_get_component", (Class<?>[]) null);
    }
    catch (Exception e) {
      System.err.println("_get_component nao foi definida para a classe "
        + name + "!");
    }
  }

  /**
   * Popula os mapas do contexto. Cria as facetas, caso n�o tenham sido
   * fornecidas.
   * 
   * @param context
   * @param facetExtDescs
   * @param facetDescs
   * @param receptDescs
   * @throws IllegalArgumentException
   * @throws ServantNotActive
   * @throws WrongPolicy
   * @throws BadKind
   * @throws NoSuchMethodException
   * @throws InvocationTargetException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws ClassNotFoundException
   * @throws SecurityException
   */
  private void assemble(ComponentContext context,
    ExtendedFacetDescription[] facetExtDescs, FacetDescription[] facetDescs,
    ReceptacleDescription[] receptDescs) throws IllegalArgumentException,
    ServantNotActive, WrongPolicy, BadKind, SecurityException,
    ClassNotFoundException, InstantiationException, IllegalAccessException,
    InvocationTargetException, NoSuchMethodException {
    // extended facet descriptions
    if (facetExtDescs != null)
      for (int i = 0; i < facetExtDescs.length; i++) {
        context.getExtendedFacetDescs().put(facetExtDescs[i].name,
          facetExtDescs[i]);
      }
    else
      throw new IllegalArgumentException();

    // facet descriptions and facets
    if (facetDescs != null)
      for (int i = 0; i < facetDescs.length; i++) {
        context.getFacetDescs().put(facetDescs[i].name, facetDescs[i]);
        context.getFacets().put(facetDescs[i].name, facetDescs[i].facet_ref);
      }
    else
      createFacetDescriptions(context, facetExtDescs);

    // receptacle descriptions and receptacles
    if (receptDescs != null)
      for (int i = 0; i < receptDescs.length; i++) {
        Receptacle rcpt = new Receptacle(receptDescs[i]);
        context.getReceptacles().put(receptDescs[i].name, rcpt);
        context.getReceptacleDescs().put(receptDescs[i].name, receptDescs[i]);
      }

    // always try to add basic facets
    addBasicFacets(context);
  }

  private void createFacetDescriptions(ComponentContext context,
    ExtendedFacetDescription[] facetExtDescs) throws ClassNotFoundException,
    IllegalArgumentException, SecurityException, InstantiationException,
    IllegalAccessException, InvocationTargetException, NoSuchMethodException,
    ServantNotActive, WrongPolicy {
    ExtendedFacetDescription extDesc;
    if (facetExtDescs != null) {
      for (int i = 0; i < facetExtDescs.length; i++) {
        extDesc = facetExtDescs[i];
        Class<?> c =
          Class.forName(extDesc.class_name, true, Thread.currentThread()
            .getContextClassLoader());
        testForGetComponent(c, extDesc.class_name);
        Object facet =
          c.getConstructor(ComponentContext.class).newInstance(context);
        if (facet instanceof Servant) {
          addFacetDescToComponent(context, extDesc, (Servant) facet);
        }
      }
    }
  }

  private void addFacetDescToComponent(ComponentContext context,
    ExtendedFacetDescription extDesc, Servant facet) throws ServantNotActive,
    WrongPolicy {
    FacetDescription desc = new FacetDescription();
    desc.name = extDesc.name;
    desc.interface_name = extDesc.interface_name;
    desc.facet_ref = poa.servant_to_reference(facet);
    context.getFacetDescs().put(desc.name, desc);
    context.getFacets().put(extDesc.name, facet);
  }

  /**
   * Adiciona facetas b�sicas a um componente.
   * 
   * @param context
   * @throws ServantNotActive
   * @throws WrongPolicy
   * @throws BadKind
   */
  private void addBasicFacets(ComponentContext context)
    throws ServantNotActive, WrongPolicy, BadKind {
    boolean hasIC = false;
    boolean hasIR = false;
    boolean hasIM = false;
    String icId = IComponentHelper.id();
    String irId = IReceptaclesHelper.id();
    String imId = IMetaInterfaceHelper.id();
    for (FacetDescription desc : context.getFacetDescs().values()) {
      if (desc.interface_name == icId) {
        hasIC = true;
      }
      if (desc.interface_name == irId) {
        hasIR = true;
      }
      if (desc.interface_name == imId) {
        hasIM = true;
      }
    }
    if (!hasIC) {
      IComponentServant facet = new IComponentServant(context);
      ExtendedFacetDescription extDesc =
        new ExtendedFacetDescription(IComponentHelper.type().name(),
          IComponentHelper.id(), IComponentServant.class.getSimpleName());
      addFacetDescToComponent(context, extDesc, facet);
      context.getExtendedFacetDescs().put(extDesc.name, extDesc);
    }
    if (!hasIR) {
      IReceptaclesServant facet = new IReceptaclesServant(context);
      ExtendedFacetDescription extDesc =
        new ExtendedFacetDescription(IReceptaclesHelper.type().name(),
          IReceptaclesHelper.id(), IReceptaclesServant.class.getSimpleName());
      addFacetDescToComponent(context, extDesc, facet);
      context.getExtendedFacetDescs().put(extDesc.name, extDesc);
    }
    if (!hasIM) {
      IMetaInterfaceServant facet = new IMetaInterfaceServant(context);
      ExtendedFacetDescription extDesc =
        new ExtendedFacetDescription(IMetaInterfaceHelper.type().name(),
          IMetaInterfaceHelper.id(), IMetaInterfaceServant.class
            .getSimpleName());
      addFacetDescToComponent(context, extDesc, facet);
      context.getExtendedFacetDescs().put(extDesc.name, extDesc);
    }
  }

  /**
   * Cria um componente.
   * 
   * @param facetExtDescs Descri��es das facetas
   * @param id
   * @return O componentContext
   * @throws ServantNotActive
   * @throws WrongPolicy
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws IllegalArgumentException
   * @throws SecurityException
   * @throws InvocationTargetException
   * @throws NoSuchMethodException
   * @throws BadKind
   */
  public ComponentContext newComponent(
    ExtendedFacetDescription[] facetExtDescs, ComponentId id)
    throws ServantNotActive, WrongPolicy, InstantiationException,
    IllegalAccessException, ClassNotFoundException, IllegalArgumentException,
    SecurityException, InvocationTargetException, NoSuchMethodException,
    BadKind {
    return newComponent(facetExtDescs, null, id, null);
  }

  /**
   * Cria um componente a partir das descri��es das portas (facetas e
   * recept�culos). Todas as descri��es ser�o inclu�das no
   * {@link ComponentContext} para uso posterior pelo programador.
   * 
   * @param facetExtDescs Descri��es extendidas contendo o nome can�nico da
   *        classe que implementa cada faceta. Esse nome can�nico � usado para
   *        construir o objeto de cada faceta passando o contexto do componente
   * @param receptacleDescs Descri��es dos recept�culos, conforme
   *        {@link ReceptacleDescription}
   * @param id Identificador do componente
   * @return context Contexto do componente onde as descri��es ser�o
   *         armazenadas.
   * @throws ServantNotActive
   * @throws WrongPolicy
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws IllegalArgumentException
   * @throws SecurityException
   * @throws InvocationTargetException
   * @throws NoSuchMethodException
   * @throws BadKind
   */
  public ComponentContext newComponent(
    ExtendedFacetDescription[] facetExtDescs,
    ReceptacleDescription[] receptacleDescs, ComponentId id)
    throws ServantNotActive, WrongPolicy, InstantiationException,
    IllegalAccessException, ClassNotFoundException, IllegalArgumentException,
    SecurityException, InvocationTargetException, NoSuchMethodException,
    BadKind {
    return newComponent(facetExtDescs, receptacleDescs, id, null);
  }

  /**
   * Cria um componente.
   * 
   * @param facetExtDescs Descri��es das facetas
   * @param receptacleDescs Descri��es dos recept�culos
   * @param id
   * @param context A inst�ncia de ComponentContext a ser utilizada.
   * @return O componentContext
   * @throws ServantNotActive
   * @throws WrongPolicy
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws IllegalArgumentException
   * @throws SecurityException
   * @throws InvocationTargetException
   * @throws NoSuchMethodException
   * @throws BadKind
   */
  public ComponentContext newComponent(
    ExtendedFacetDescription[] facetExtDescs,
    ReceptacleDescription[] receptacleDescs, ComponentId id,
    ComponentContext context) throws ServantNotActive, WrongPolicy,
    InstantiationException, IllegalAccessException, ClassNotFoundException,
    IllegalArgumentException, SecurityException, InvocationTargetException,
    NoSuchMethodException, BadKind {

    if (context == null) {
      context = createContext(id);
    }

    // We use simple arrays so the user can choose a load order
    assemble(context, facetExtDescs, null, receptacleDescs);

    return context;
  }

  /**
   * Cria um componente a partir de facetas j� instanciadas.
   * 
   * @param facetDescs Descri��es das facetas
   * @param receptacleDescs Descri��es dos recept�culos
   * @param id
   * @param context Opcional. A inst�ncia de ComponentContext a ser utilizada.
   * @return O componentContext
   * @throws ServantNotActive
   * @throws WrongPolicy
   * @throws BadKind
   * @throws IllegalArgumentException
   * @throws SecurityException
   * @throws NoSuchMethodException
   * @throws InvocationTargetException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws ClassNotFoundException
   */
  public ComponentContext newComponent(FacetDescription[] facetDescs,
    ReceptacleDescription[] receptacleDescs, ComponentId id,
    ComponentContext context) throws ServantNotActive, WrongPolicy, BadKind,
    IllegalArgumentException, SecurityException, ClassNotFoundException,
    InstantiationException, IllegalAccessException, InvocationTargetException,
    NoSuchMethodException {

    if (context == null) {
      context = createContext(id);
    }

    // create extended facet descriptions
    ExtendedFacetDescription[] facetExtDescs =
      new ExtendedFacetDescription[facetDescs.length];
    FacetDescription desc;
    for (int i = 0; i < facetDescs.length; i++) {
      desc = facetDescs[i];
      String className = desc.facet_ref.getClass().getCanonicalName();
      ExtendedFacetDescription extDesc =
        new ExtendedFacetDescription(desc.name, desc.interface_name, className);
      facetExtDescs[i] = extDesc;
    }

    // tests for _get_component()
    assemble(context, facetExtDescs, facetDescs, receptacleDescs);

    return context;
  }

  /**
   * Desativa todas as facetas do componente. As refer�ncias facet_ref
   * continuam n�o-nulas ap�s a chamada, para manter o acesso ao objeto Java.
   * O usu�rio fica respons�vel por reativar as facetas quando considerar 
   * apropriado.
   * 
   * @param context Inst�ncia do componente.
   * 
   * @return Mapa contendo os nomes das facetas(chaves) e as mensagens de
   *         erro(valores) das facetas que n�o puderam ser desativadas.
   */
  public Map<String, String> deactivateComponent(ComponentContext context) {
    Map<String, String> errMsgs = new HashMap<String, String>();
    for (FacetDescription desc : context.getFacetDescs().values()) {
      try {
        this.poa.deactivate_object(this.poa.reference_to_id(desc.facet_ref));
      }
      catch (Exception e) {
        e.printStackTrace();
        errMsgs.put(desc.name, e.getMessage());
      }
    }
    return errMsgs;
  }

  /**
   * Cria a chave para o mapa de componentes
   * 
   * @param id Identificador do componente
   * @return String concatenando o nome do componente com a sua vers�o
   */
  public static String componentIdToString(ComponentId compid) {
    return compid.name + String.valueOf(compid.major_version)
      + String.valueOf(compid.minor_version)
      + String.valueOf(compid.patch_version);
  }

  /**
   * Obt�m a refer�ncia ao POA
   * 
   * @return Inst�ncia do POA
   */
  public POA getPOA() {
    return poa;
  }

  /**
   * Obt�m a refer�ncia ao ORB
   * 
   * @return Inst�ncia do ORB
   */
  public ORB getORB() {
    return orb;
  }
}
