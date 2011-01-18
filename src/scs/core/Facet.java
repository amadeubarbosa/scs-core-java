package scs.core;

import org.omg.PortableServer.Servant;

/**
 * 
 * Não colocamos um campo FacetDescription direto pois a classe tem os campos
 * públicos. O getter forneceria a classe que poderia ter seus dados
 * modificados.
 * 
 * @author cadu
 * 
 */
public final class Facet {
  private String name;
  private String interface_name;
  private org.omg.CORBA.Object facet_ref;
  private Servant servant;

  Facet(String name, String interface_name,
    org.omg.CORBA.Object facet_ref, Servant servant) {
    this.name = name;
    this.interface_name = interface_name;
    this.facet_ref = facet_ref;
    this.servant = servant;
  }

  /**
   * @return FacetDescription
   */
  public FacetDescription getDescription() {
    return new FacetDescription(name, interface_name, facet_ref);
  }

  public String getName() {
    return name;
  }

  public String getInterfaceName() {
    return interface_name;
  }

  /**
   * @return Servant
   */
  public Servant getServant() {
    return servant;
  }
}
