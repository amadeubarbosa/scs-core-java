package scs.core;

import java.util.Collection;
import java.util.Iterator;

/**
 * This class is the basic implementation of the IDL interface
 * {@link IMetaInterface}. The IMetaInterface interface provides access to the
 * component's metadata, acting as an introspection facet.
 */
public class IMetaInterfaceServant extends scs.core.IMetaInterfacePOA {

  /**
   * Reference to the context of this facet, i.e., the local representation of
   * its component.
   */
  protected ComponentContext myComponent;

  /**
   * Primary constructor.
   * 
   * @param myComponent The component that owns this facet instance.
   */
  public IMetaInterfaceServant(ComponentContext myComponent) {
    this.myComponent = myComponent;
  }

  /**
   * Provides CORBA references and metadata of all of the component's facets.
   * 
   * @return An array with the facets metadata.
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
   * Provides CORBA references and metadata of some of the component's facets,
   * specified by their names.
   * 
   * @param names The names of the desired facets.
   * @return An array with the specified facets metadata.
   * @see IMetaInterfaceOperations#getFacetsByName(String[] names)
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
   * Provides metadata of all of the component's receptacles.
   * 
   * @return An array with the receptacles metadata.
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
   * Provides metadata of some of the component's receptacles, specified by
   * their names.
   * 
   * @param names The names of the desired receptacles.
   * @return An array with the specified receptacles metadata.
   * @see IMetaInterfaceOperations#getReceptaclesByName(String[] names)
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
   * Provides the reference to the most basic facet of the component,
   * IComponent.
   */
  @Override
  public org.omg.CORBA.Object _get_component() {
    return this.myComponent.getIComponent();
  }
}
