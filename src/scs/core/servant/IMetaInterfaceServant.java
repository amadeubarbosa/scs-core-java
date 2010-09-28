package scs.core.servant;

import java.util.Collection;

import scs.core.FacetDescription;
import scs.core.IMetaInterface;
import scs.core.IMetaInterfaceOperations;
import scs.core.InvalidName;
import scs.core.ReceptacleDescription;

/**
 * Servant da interface IDL {@link IMetaInterface}. Implementa as
 * características comuns a todos IMetaInterface.
 * 
 * @author Eduardo Fonseca/Luiz Marques
 * 
 */
public class IMetaInterfaceServant extends scs.core.IMetaInterfacePOA {

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
  public IMetaInterfaceServant(ComponentContext myComponent) {
    this.myComponent = myComponent;
  }

  /**
   * Listagem das descrições de facetas
   * 
   * @see IMetaInterfaceOperations#getFacets()
   */
  public FacetDescription[] getFacets() {
    Collection<FacetDescription> descs = myComponent.getFacetDescs().values();
    return descs.toArray(new FacetDescription[descs.size()]);
  }

  /**
   * Busca de descrições de facetas a partir de uma lista de nomes fictícios
   * 
   * @see IMetaInterfaceOperations#getFacetsByName(String[])
   */
  public FacetDescription[] getFacetsByName(String[] names) throws InvalidName {
    FacetDescription[] facets = new FacetDescription[names.length];
    for (int i = 0; i < names.length; i++) {
      facets[i] = myComponent.getFacetDescs().get(names[i]);
      if (facets[i] == null)
        throw new InvalidName(names[i]);
    }
    return facets;
  }

  /**
   * Listagem de todas descrições de receptáculos
   * 
   * @see IMetaInterfaceOperations#getReceptacles()
   */
  public ReceptacleDescription[] getReceptacles() {
    Collection<ReceptacleDescription> descs =
      myComponent.getReceptacleDescs().values();
    return descs.toArray(new ReceptacleDescription[descs.size()]);
  }

  /**
   * Busca de descrições de receptáculos a partir de uma lista de nomes
   * fictícios
   * 
   * @see IMetaInterfaceOperations#getReceptaclesByName(String[])
   */
  public ReceptacleDescription[] getReceptaclesByName(String[] names)
    throws InvalidName {
    ReceptacleDescription[] receptacles =
      new ReceptacleDescription[names.length];
    for (int i = 0; i < names.length; i++) {
      receptacles[i] = myComponent.getReceptacleDescs().get(names[i]);
      if (receptacles[i] == null)
        throw new InvalidName(names[i]);
    }
    return receptacles;
  }

  /**
   * Retorna a referência para a faceta IComponent. Específico do JACORB.
   */
  @Override
  public org.omg.CORBA.Object _get_component() {
    return this.myComponent.getIComponent();
  }
}
