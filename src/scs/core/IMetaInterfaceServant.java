package scs.core;

import java.util.Collection;

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
    Collection<Facet> facets = myComponent.getFacets();
    FacetDescription[] descs = new FacetDescription[facets.size()];
    int i = 0;
    for (Facet facet : facets) {
      descs[i] = facet.getDescription();
      i++;
    }

    return descs;
  }

  /**
   * Busca de descrições de facetas a partir de uma lista de nomes fictícios
   * 
   * @see IMetaInterfaceOperations#getFacetsByName(String[])
   */
  public FacetDescription[] getFacetsByName(String[] names) throws InvalidName {
    FacetDescription[] facets = new FacetDescription[names.length];
    for (int i = 0; i < names.length; i++) {
      facets[i] = myComponent.getFacetByName(names[i]).getDescription();
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
    Collection<Receptacle> receptacles = myComponent.getReceptacles();
    ReceptacleDescription[] descs =
      new ReceptacleDescription[receptacles.size()];
    int i = 0;
    for (Receptacle receptacle : receptacles) {
      descs[i] = receptacle.getDescription();
      i++;
    }

    return descs;
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
      receptacles[i] =
        myComponent.getReceptacleByName(names[i]).getDescription();
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
