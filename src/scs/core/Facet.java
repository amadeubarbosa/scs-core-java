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
  private String interfaceName;
  private org.omg.CORBA.Object reference;
  private Servant servant;

  Facet(String name, String interfaceName, org.omg.CORBA.Object referece,
    Servant servant) {
    if (name == null) {
      throw new IllegalArgumentException("The name can't be null");
    }
    if (interfaceName == null) {
      throw new IllegalArgumentException("The interface's name can't be null");
    }
    if (referece == null) {
      throw new IllegalArgumentException("The reference can't be null");
    }
    if (servant == null) {
      throw new IllegalArgumentException("The servant can't be null");
    }

    this.name = name;
    this.interfaceName = interfaceName;
    this.reference = referece;
    this.servant = servant;
  }

  /**
   * @return FacetDescription
   */
  public FacetDescription getDescription() {
    return new FacetDescription(name, interfaceName, reference);
  }

  public String getName() {
    return name;
  }

  public String getInterfaceName() {
    return interfaceName;
  }

  public org.omg.CORBA.Object getReference() {
    return reference;
  }

  /**
   * @return Servant
   */
  public Servant getServant() {
    return servant;
  }
}
