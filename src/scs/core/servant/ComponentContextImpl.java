package scs.core.servant;

import java.util.HashMap;
import java.util.Map;

import scs.core.ComponentId;
import scs.core.FacetDescription;
import scs.core.IComponent;
import scs.core.ReceptacleDescription;

public class ComponentContextImpl implements ComponentContext {
  private ComponentId componentId;
  // facetDescs contain CORBA objects (field facet_ref)
  private Map<String, ExtendedFacetDescription> extFacetDescs;
  private Map<String, FacetDescription> facetDescs;
  private Map<String, ReceptacleDescription> receptacleDescs;
  // facets contain JAVA objects (servants)
  private Map<String, java.lang.Object> facets;
  private Map<String, Receptacle> receptacles;
  private ComponentBuilder builder;

  /**
   * Method that serves as a factory for the component.
   * 
   * @return A new component
   */
  public ComponentContextImpl(ComponentBuilder builder, ComponentId id) {
    this.builder = builder;
    facetDescs = new HashMap<String, FacetDescription>();
    extFacetDescs = new HashMap<String, ExtendedFacetDescription>();
    facets = new HashMap<String, java.lang.Object>();
    receptacleDescs = new HashMap<String, ReceptacleDescription>();
    receptacles = new HashMap<String, Receptacle>();
    componentId = id;
  }

  /*
   * (non-Javadoc)
   * 
   * @see scs.core.servant.IComponentContext#getComponentId()
   */
  public ComponentId getComponentId() {
    return componentId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see scs.core.servant.IComponentContext#getFacetDescs()
   */
  public Map<String, FacetDescription> getFacetDescs() {
    return facetDescs;
  }

  /*
   * (non-Javadoc)
   * 
   * @see scs.core.servant.IComponentContext#getExtendedFacetDescs()
   */
  public Map<String, ExtendedFacetDescription> getExtendedFacetDescs() {
    return extFacetDescs;
  }

  /*
   * (non-Javadoc)
   * 
   * @see scs.core.servant.IComponentContext#getReceptacleDescs()
   */
  public Map<String, ReceptacleDescription> getReceptacleDescs() {
    return receptacleDescs;
  }

  /*
   * (non-Javadoc)
   * 
   * @see scs.core.servant.IComponentContext#getFacets()
   */
  public Map<String, Object> getFacets() {
    return facets;
  }

  /*
   * (non-Javadoc)
   * 
   * @see scs.core.servant.IComponentContext#getReceptacles()
   */
  public Map<String, Receptacle> getReceptacles() {
    return receptacles;
  }

  /*
   * (non-Javadoc)
   * 
   * @see scs.core.servant.IComponentContext#getBase()
   */
  public ComponentBuilder getBuilder() {
    return builder;
  }

  /*
   * (non-Javadoc)
   * 
   * @see scs.core.servant.IComponentContext#getIComponent()
   */
  public org.omg.CORBA.Object getIComponent() {
    FacetDescription desc =
      this.facetDescs.get(IComponent.class.getSimpleName());
    if (desc != null) {
      return desc.facet_ref;
    }
    return null;
  }
}
