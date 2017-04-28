package scs.core;

import org.omg.CORBA.UserException;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;
import scs.core.exception.SCSException;

/**
 * This class holds all the metadata pertinent to a facet, and represents it
 * locally.
 * 
 * It doesn't maintain a FacetDescription directly because the FacetDescription
 * is an automatically generated class. The generated code sets all of its
 * fields as public but these fields should not be manipulated freely.
 */
public final class Facet {
  POA poa;
  /**
   * The name of the facet, which acts as its identifier within the component.
   */
  private String name;

  /**
   * The IDL interface that this facet represents.
   */
  private String interfaceName;

  /**
   * The Servant instance that implements the interface.
   */
  private Servant servant;

  /**
   * The object ID produced by POA or specified by user on object activation process.
   */
  private boolean userIdAssignmentPolicy;

  /**
   * The CORBA object.
   */
  private org.omg.CORBA.Object reference;


  Facet(POA poa, String name, String interfaceName, Servant servant)
          throws SCSException {
    this(poa, name, interfaceName, servant, false);
  }
  Facet(POA poa, String name, String interfaceName, Servant servant, boolean userIdAssignmentPolicy)
    throws SCSException {
    if (poa == null) {
      throw new IllegalArgumentException("The poa can't be null");
    }
    if (name == null) {
      throw new IllegalArgumentException("The name can't be null");
    }
    if (interfaceName == null) {
      throw new IllegalArgumentException("The interface's name can't be null");
    }

    this.poa = poa;
    this.name = name;
    this.interfaceName = interfaceName;
    this.userIdAssignmentPolicy = userIdAssignmentPolicy;
    this.setServant(servant);
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

  void setServant(Servant servant) throws SCSException {
    if (servant == null) {
      throw new IllegalArgumentException("The servant can't be null");
    }
    if (this.servant != null) {
      this.deactivate();
    }
    this.servant = servant;
    this.checkForGetComponent();
    this.activate();
  }

  private void checkForGetComponent() {
    try {
      this.getClass().getMethod("_get_component");
    }
    catch (NoSuchMethodException e) {
      //TODO: logar como warn que _get_component não foi definida.
    }
  }

  private void activate() throws SCSException {
    try {
      // POA policies sanit check
      if (this.userIdAssignmentPolicy) {
        this.poa.activate_object_with_id(this.name.getBytes(), this.servant);
        this.reference = this.poa.id_to_reference(this.name.getBytes());
      } else {
        // implicit activation
        this.reference = this.poa.servant_to_reference(this.servant);
      }
    }
    catch (UserException e) {
      throw new SCSException(e);
    }
  }

  void deactivate() throws SCSException {
    try {
      byte[] referenceId = this.poa.reference_to_id(this.reference);
      this.poa.deactivate_object(referenceId);
    }
    catch (UserException e) {
      throw new SCSException(e);
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }

    if (!obj.getClass().equals(this.getClass())) {
      return false;
    }

    Facet facet = (Facet) obj;
    return (this.name.equals(facet.name));
  }

  @Override
  public int hashCode() {
    return this.name.hashCode();
  }
}
