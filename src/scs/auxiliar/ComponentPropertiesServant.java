package scs.auxiliar;

import java.util.HashMap;
import java.util.Map;

import scs.core.ComponentContext;

/**
 * This class is an implementation of the {@link ComponentProperties} CORBA
 * interface. The mentioned interface provides access to exported properties of
 * a component.
 * 
 */
public class ComponentPropertiesServant extends ComponentPropertiesPOA {

  /**
   * Reference to the component instance that this implementation belongs to.
   */
  private ComponentContext myComponent;

  /**
   * Map that associates property names with instances of the Property structure
   * defined in IDL.
   */
  private Map<String, Property> props;

  /**
   * Primary constructor. Initializes a map that associates property names with
   * instances of the Property structure defined in IDL.
   * 
   * @param myComponent Reference to the component instance that this
   *        implementation belongs to.
   */
  public ComponentPropertiesServant(ComponentContext myComponent) {
    this.myComponent = myComponent;
    this.props = new HashMap<String, Property>();
  }

  /**
   * Provides an array with all the component's properties.
   * 
   * @return A sequence with all properties.
   */
  public Property[] getProperties() {
    return props.values().toArray(new Property[props.size()]);
  }

  /**
   * Given a property name, returns its value.
   * 
   * @param name The property name.
   * @return The desired property.
   * @throws UndefinedProperty If the property name does not exist.
   */
  public Property getProperty(String name) throws UndefinedProperty {
    Property value = props.get(name);
    if (value != null)
      return value;
    throw new UndefinedProperty();
  }

  /**
   * Given a property name, sets a new value. If the property doesn't exist,
   * does nothing.
   * 
   * @param prop The property with name and value to be set.
   * @throws ReadOnlyProperty If the property cannot be changed.
   */
  public void setProperty(Property prop) throws ReadOnlyProperty {
    Property value = props.get(prop.name);
    if (value != null)
      if (!value.read_only)
        props.put(prop.name, prop);
      else
        throw new ReadOnlyProperty();
  }

  /**
   * Provides a reference for the IComponent facet of this component.
   */
  @Override
  public org.omg.CORBA.Object _get_component() {
    return this.myComponent.getIComponent();
  }
}
