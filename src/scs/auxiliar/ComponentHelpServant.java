package scs.auxiliar;

import java.util.HashMap;
import java.util.Map;

import scs.core.ComponentContext;
import scs.core.ComponentId;

/**
 * This class is an implementation of the {@link ComponentHelp} CORBA interface.
 * The mentioned interface provides help strings for a collection of components.
 * 
 */
public class ComponentHelpServant extends ComponentHelpPOA {

  /**
   * Reference to the component instance that this implementation belongs to.
   */
  private ComponentContext myComponent;

  /**
   * Map that associates help strings with component id's.
   */
  private Map<String, String> help;

  /**
   * Primary constructor. Initializes a map that associates help strings with
   * component id's.
   * 
   * @param myComponent Reference to the component instance that this
   *        implementation belongs to.
   */
  public ComponentHelpServant(ComponentContext myComponent) {
    this.myComponent = myComponent;
    this.help = new HashMap<String, String>();
  }

  /**
   * Provides help info for a specific component.
   * 
   * @param id The component's id.
   * @return Help string.
   */
  public String getHelpInfo(ComponentId id) throws HelpInfoNotAvailable {
    return help.get(id.name);
  }

  /**
   * Provides a reference for the IComponent facet of this component.
   */
  @Override
  public org.omg.CORBA.Object _get_component() {
    return this.myComponent.getIComponent();
  }
}
