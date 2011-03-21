package scs.core;

/**
 * This class is the basic implementation of the IDL interface
 * {@link IComponent}. The IComponent interface is the CORBA representation of a
 * SCS component.
 */
public class IComponentServant extends IComponentPOA {

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
  public IComponentServant(ComponentContext myComponent) {
    this.myComponent = myComponent;
  }

  /**
   * Provides the CORBA reference to one of the component's facets, by its
   * interface. Since there can be more than one facet with the same interface,
   * there's no guarantee on which one of these will be returned.
   * 
   * @param facet_interface The facet interface.
   * @return The CORBA object that represents the facet. If there's more than
   *         one facet with the specified interface, any one of them may be
   *         returned. If there's no facet with the specified interface, null is
   *         returned.
   * @see scs.core.IComponentOperations#getFacet(java.lang.String)
   */
  public org.omg.CORBA.Object getFacet(String facet_interface) {
    for (Facet facet : myComponent.getFacets()) {
      FacetDescription desc = facet.getDescription();
      if (desc.interface_name.equals(facet_interface))
        return desc.facet_ref;
    }
    return null;
  }

  /**
   * Provides the CORBA reference to one of the component's facets, by its name.
   * There cannot be more than one facet with the same name, so there's only one
   * possible return value.
   * 
   * @param facetName The facet name.
   * @return The CORBA object that represents the facet. If there's no facet
   *         with the specified interface, null is returned.
   * @see scs.core.IComponentOperations#getFacetByName(java.lang.String)
   */
  public org.omg.CORBA.Object getFacetByName(String facetName) {
    Facet facet = myComponent.getFacetByName(facetName);
    if (facet == null) {
      return null;
    }
    return facet.getReference();
  }

  /**
   * Empty implementation. This method should be overridden by the user if
   * component initialization is required.
   * 
   * @see scs.core.IComponentOperations#startup()
   */
  public void startup() throws StartupFailed {
  }

  /**
   * Empty implementation. This method should be overridden by the user if
   * component finalization is required.
   * 
   * @see scs.core.IComponentOperations#shutdown()
   */
  public void shutdown() throws ShutdownFailed {
  }

  /**
   * Provides the component's ComponentId. ComponentId's aren't instance
   * identifiers; they specify a component's name, version and platform
   * specification.
   * 
   * @return The ComponentId.
   */
  public ComponentId getComponentId() {
    return myComponent.getComponentId();
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
