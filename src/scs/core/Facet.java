package scs.core;

import org.omg.PortableServer.Servant;

/**
 * This class holds all the metadata pertinent to a facet, and represents it
 * locally.
 * 
 * It doesn't maintain a FacetDescription directly because the FacetDescription
 * is an automatically generated class. The generated code sets all of its
 * fields as public but these fields should not be manipulated freely.
 */
public final class Facet {
  /**
   * The name of the facet, which acts as its identifier within the component.
   */
  private String name;

  /**
   * The IDL interface that this facet represents.
   */
  private String interfaceName;

  /**
   * The CORBA object.
   */
  private org.omg.CORBA.Object reference;

  /**
   * The Servant instance that implements the interface.
   */
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
   * Provides the IDL structure FacetDescription of this facet.
   * 
   * @return FacetDescription The description.
   */
  public FacetDescription getDescription() {
    return new FacetDescription(name, interfaceName, reference);
  }

  /**
   * Provides the name of this facet.
   * 
   * @return The facet name.
   */
  public String getName() {
    return name;
  }

  /**
   * Provides the interface of this facet.
   * 
   * @return The facet interface.
   */
  public String getInterfaceName() {
    return interfaceName;
  }

  /**
   * Provides the CORBA reference of this facet, as a CORBA object.
   * 
   * @return The CORBA object.
   */
  public org.omg.CORBA.Object getReference() {
    return reference;
  }

  /**
   * Provides the Servant instance of this facet, which is the Java class that
   * implements the facet interface.
   * 
   * @return Servant The Java Servant.
   */
  public Servant getServant() {
    return servant;
  }
}
