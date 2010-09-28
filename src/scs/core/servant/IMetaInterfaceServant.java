package scs.core.servant;

import java.util.Collection;

import scs.core.FacetDescription;
import scs.core.IMetaInterface;
import scs.core.IMetaInterfaceOperations;
import scs.core.InvalidName;
import scs.core.ReceptacleDescription;

/**
 * Servant da interface IDL {@link IMetaInterface}. Implementa as
 * caracter�sticas comuns a todos IMetaInterface.
 * 
 * @author Eduardo Fonseca/Luiz Marques
 * 
 */
public class IMetaInterfaceServant extends scs.core.IMetaInterfacePOA {

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
  public IMetaInterfaceServant(ComponentContext myComponent) {
    this.myComponent = myComponent;
  }

  /**
   * Listagem das descri��es de facetas
   * 
   * @see IMetaInterfaceOperations#getFacets()
   */
  public FacetDescription[] getFacets() {
    Collection<FacetDescription> descs = myComponent.getFacetDescs().values();
    return descs.toArray(new FacetDescription[descs.size()]);
  }

  /**
   * Busca de descri��es de facetas a partir de uma lista de nomes fict�cios
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
   * Listagem de todas descri��es de recept�culos
   * 
   * @see IMetaInterfaceOperations#getReceptacles()
   */
  public ReceptacleDescription[] getReceptacles() {
    Collection<ReceptacleDescription> descs =
      myComponent.getReceptacleDescs().values();
    return descs.toArray(new ReceptacleDescription[descs.size()]);
  }

  /**
   * Busca de descri��es de recept�culos a partir de uma lista de nomes
   * fict�cios
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
   * Retorna a refer�ncia para a faceta IComponent. Espec�fico do JACORB.
   */
  @Override
  public org.omg.CORBA.Object _get_component() {
    return this.myComponent.getIComponent();
  }
}
