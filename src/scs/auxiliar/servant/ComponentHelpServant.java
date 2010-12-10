package scs.auxiliar.servant;

import java.util.HashMap;
import java.util.Map;

import scs.auxiliar.ComponentHelp;
import scs.auxiliar.ComponentHelpPOA;
import scs.auxiliar.HelpInfoNotAvailable;
import scs.core.ComponentContext;
import scs.core.ComponentId;

/**
 * Servant da interface IDL {@link ComponentHelp}
 * 
 * @author Carlos Eduardo / (coment�rios) Amadeu A. Barbosa Jr
 * 
 */
public class ComponentHelpServant extends ComponentHelpPOA {

  private ComponentContext myComponent;
  private Map<String, String> help;

  public ComponentHelpServant(ComponentContext myComponent) {
    this.myComponent = myComponent;
    this.help = new HashMap<String, String>();
  }

  /**
   * M�todo para obter a string auxiliar de um componente
   * 
   * @param id Identificador do componente
   * @return String de aux�lio
   */
  public String getHelpInfo(ComponentId id) throws HelpInfoNotAvailable {
    return help.get(id.name);
  }

  /**
   * Retorna a refer�ncia para a faceta IComponent. Espec�fico do JACORB.
   */
  @Override
  public org.omg.CORBA.Object _get_component() {
    return this.myComponent.getIComponent();
  }
}
