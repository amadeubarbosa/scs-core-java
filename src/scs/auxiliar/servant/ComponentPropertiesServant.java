package scs.auxiliar.servant;

import java.util.HashMap;
import java.util.Map;

import scs.auxiliar.ComponentProperties;
import scs.auxiliar.ComponentPropertiesPOA;
import scs.auxiliar.Property;
import scs.auxiliar.ReadOnlyProperty;
import scs.auxiliar.UndefinedProperty;
import scs.core.servant.ComponentContext;

/**
 * Servant da inteface IDL {@link ComponentProperties}
 * 
 * @author Carlos Eduardo / (comentários) Amadeu A. Barbosa Júnior
 * 
 */
public class ComponentPropertiesServant extends ComponentPropertiesPOA {

  private ComponentContext myComponent;
  private Map<String, Property> props;

  public ComponentPropertiesServant(ComponentContext myComponent) {
    this.myComponent = myComponent;
    this.props = new HashMap<String, Property>();
  }

  @Override
  public Property[] getProperties() {
    return props.values().toArray(new Property[props.size()]);
  }

  @Override
  public Property getProperty(String name) throws UndefinedProperty {
    Property value = props.get(name);
    if (value != null)
      return value;
    throw new UndefinedProperty();
  }

  @Override
  public void setProperty(Property prop) throws ReadOnlyProperty {
    Property value = props.get(prop.name);
    if (value != null)
      if (!value.read_only)
        props.put(prop.name, prop);
      else
        throw new ReadOnlyProperty();
  }

  /**
   * Retorna a referência para a faceta IComponent. Específico do JACORB.
   */
  @Override
  public org.omg.CORBA.Object _get_component() {
    return this.myComponent.getIComponent();
  }
}
