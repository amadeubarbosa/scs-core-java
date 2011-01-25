package scs.core;

import java.util.Collection;
import java.util.Iterator;

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
    FacetDescription[] descriptions = new FacetDescription[facets.size()];
    Iterator<Facet> facetsIterator = facets.iterator();
    for (int i = 0; i < descriptions.length; i++) {
      descriptions[i] = facetsIterator.next().getDescription();
    }
    return descriptions;
  }

  /**
   * Busca de descrições de facetas a partir de uma lista de nomes fictícios
   * 
   * @see IMetaInterfaceOperations#getFacetsByName(String[])
   */
  public FacetDescription[] getFacetsByName(String[] names) throws InvalidName {
    FacetDescription[] facets = new FacetDescription[names.length];
    for (int i = 0; i < names.length; i++) {
      Facet facet = myComponent.getFacetByName(names[i]);
      if (facet == null) {
        throw new InvalidName(names[i]);
      }
      facets[i] = facet.getDescription();
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
    ReceptacleDescription[] descriptions =
      new ReceptacleDescription[receptacles.size()];
    Iterator<Receptacle> receptaclesIterator = receptacles.iterator();
    for (int i = 0; i < descriptions.length; i++) {
      descriptions[i] = receptaclesIterator.next().getDescription();
    }
    return descriptions;
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
      Receptacle receptacle = myComponent.getReceptacleByName(names[i]);
      if (receptacle == null) {
        throw new InvalidName(names[i]);
      }
      receptacles[i] = receptacle.getDescription();
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
