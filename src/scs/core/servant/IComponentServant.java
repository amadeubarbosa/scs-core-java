package scs.core.servant;

import scs.core.ComponentId;
import scs.core.FacetDescription;
import scs.core.IComponent;
import scs.core.IComponentPOA;
import scs.core.ShutdownFailed;
import scs.core.StartupFailed;

/**
 * Servant da interface IDL {@link IComponent}. Implementa as características
 * comuns a todos IComponent.
 * 
 * @author Carlos Eduardo
 * @author (comentários) Amadeu A. Barbosa Jr
 */
public class IComponentServant extends IComponentPOA {

  /**
   * Referência para o contexto do componente. O contexto provém facilidades
   * para acesso a dados compartilhados entre as diversas facetas
   */
  protected ComponentContext myComponent;

  /**
   * Construtor padrão usado pela infra-estrutura do SCS durante a instanciação
   * automática das facetas
   * 
   * @param myComponent Contexto do componente contendo as descrições das portas
   *        (facetas e receptáculos) e métodos de ajuda que facilitam o uso da
   *        infra-estrutura
   */
  public IComponentServant(ComponentContext myComponent) {
    this.myComponent = myComponent;
  }

  /**
   * Obtém o objeto CORBA a partir do nome da interface IDL da faceta
   * 
   * @param facet_interface String da interface CORBA implementada pela faceta
   * @return Objeto CORBA que implementa a faceta
   * @see scs.core.IComponentOperations#getFacet(java.lang.String)
   */
  public org.omg.CORBA.Object getFacet(String facet_interface) {
    for (FacetDescription desc : myComponent.getFacetDescs().values()) {
      if (desc.interface_name.equals(facet_interface))
        return desc.facet_ref;
    }
    return null;
  }

  /**
   * Obtém o objeto CORBA a partir do nome fictício da faceta
   * 
   * @param facet Nome fictício da faceta
   * @return Objeto CORBA que implementa a faceta
   * @see scs.core.IComponentOperations#getFacetByName(java.lang.String)
   */
  public org.omg.CORBA.Object getFacetByName(String facet) {
    FacetDescription desc = myComponent.getFacetDescs().get(facet);
    if (desc != null)
      return desc.facet_ref;
    return null;
  }

  /**
   * Implementação vazia que pode ser sobrecarregada para permitir algum procedimento
   * específico na inicialização do IComponent
   * 
   * @see scs.core.IComponentOperations#startup()
   */
  public void startup() throws StartupFailed {
  }

  /**
   * Implementação vazia que pode ser sobrecarregada para permitir algum procedimento
   * específico na finalização do IComponent
   * 
   * @see scs.core.IComponentOperations#shutdown()
   */
  public void shutdown() throws ShutdownFailed {
  }

  /**
   * Obtém o identificador do componente
   * 
   * @return Identificador do componente
   */
  public ComponentId getComponentId() {
    return myComponent.getComponentId();
  }

  /**
   * Retorna a referência para a faceta IComponent. Específico do JACORB.
   */
  @Override
  public org.omg.CORBA.Object _get_component() {
    return this.myComponent.getIComponent();
  }
}
