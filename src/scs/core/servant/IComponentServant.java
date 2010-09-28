package scs.core.servant;

import scs.core.ComponentId;
import scs.core.FacetDescription;
import scs.core.IComponent;
import scs.core.IComponentPOA;
import scs.core.ShutdownFailed;
import scs.core.StartupFailed;

/**
 * Servant da interface IDL {@link IComponent}. Implementa as caracter�sticas
 * comuns a todos IComponent.
 * 
 * @author Carlos Eduardo
 * @author (coment�rios) Amadeu A. Barbosa Jr
 */
public class IComponentServant extends IComponentPOA {

  /**
   * Refer�ncia para o contexto do componente. O contexto prov�m facilidades
   * para acesso a dados compartilhados entre as diversas facetas
   */
  protected ComponentContext myComponent;

  /**
   * Construtor padr�o usado pela infra-estrutura do SCS durante a instancia��o
   * autom�tica das facetas
   * 
   * @param myComponent Contexto do componente contendo as descri��es das portas
   *        (facetas e recept�culos) e m�todos de ajuda que facilitam o uso da
   *        infra-estrutura
   */
  public IComponentServant(ComponentContext myComponent) {
    this.myComponent = myComponent;
  }

  /**
   * Obt�m o objeto CORBA a partir do nome da interface IDL da faceta
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
   * Obt�m o objeto CORBA a partir do nome fict�cio da faceta
   * 
   * @param facet Nome fict�cio da faceta
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
   * Implementa��o vazia que pode ser sobrecarregada para permitir algum procedimento
   * espec�fico na inicializa��o do IComponent
   * 
   * @see scs.core.IComponentOperations#startup()
   */
  public void startup() throws StartupFailed {
  }

  /**
   * Implementa��o vazia que pode ser sobrecarregada para permitir algum procedimento
   * espec�fico na finaliza��o do IComponent
   * 
   * @see scs.core.IComponentOperations#shutdown()
   */
  public void shutdown() throws ShutdownFailed {
  }

  /**
   * Obt�m o identificador do componente
   * 
   * @return Identificador do componente
   */
  public ComponentId getComponentId() {
    return myComponent.getComponentId();
  }

  /**
   * Retorna a refer�ncia para a faceta IComponent. Espec�fico do JACORB.
   */
  @Override
  public org.omg.CORBA.Object _get_component() {
    return this.myComponent.getIComponent();
  }
}
