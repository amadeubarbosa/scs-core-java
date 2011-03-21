package scs.core;

/**
 * This class is the basic implementation of the IDL interface
 * {@link IReceptacles}. The IReceptacle interface provides access to and
 * manipulation of the component's receptacles.
 */
public class IReceptaclesServant extends IReceptaclesPOA {

  /**
   * Reference to the context of this facet, i.e., the local representation of
   * its component.
   */
  private ComponentContext myComponent;

  /**
   * Primary constructor.
   * 
   * @param myComponent The component that owns this facet instance.
   */
  public IReceptaclesServant(ComponentContext myComponent) {
    this.myComponent = myComponent;
  }

  /**
   * Connects a remote facet to a receptacle. The facet must implement the
   * interface specified by the receptacle.
   * 
   * @param receptacle The receptacle name.
   * @param obj The remote facet reference.
   * @return The connection identifier. It's valid for the entire component, not
   *         for a specific receptacle.
   * @throws InvalidName If there's no receptacle with the specified name.
   * @throws InvalidConnection If the facet object does not implement the
   *         receptacle's specified interface.
   * @throws AlreadyConnected If the receptacle supports only one connection and
   *         is already connected.
   * @throws ExceededConnectionLimit If the receptacle is multiplex and the
   *         maximum number of connections was already reached.
   * @see IReceptaclesOperations#connect(String, org.omg.CORBA.Object)
   */
  public synchronized int connect(String receptacle, org.omg.CORBA.Object obj)
    throws InvalidName, InvalidConnection, AlreadyConnected,
    ExceededConnectionLimit {
    Receptacle rec = myComponent.getReceptacleByName(receptacle);
    if (rec == null)
      throw new InvalidName();

    return rec.addConnection(obj);
  }

  /**
   * Disconnects a remote facet from a receptacle. There's no need to specify
   * the receptacle.
   * 
   * @param id The connection identifier.
   * @throws InvalidConnection If the connection identifier is invalid, i.e.,
   *         less than or equal to zero.
   * @throws NoConnection If the provided connection identifier does not exist.
   */
  public synchronized void disconnect(int id) throws InvalidConnection,
    NoConnection {
    if (id <= 0) {
      throw new InvalidConnection();
    }

    boolean disconnected = false;
    for (Receptacle receptacle : myComponent.getReceptacles()) {
      if (receptacle.removeConnection(id)) {
        disconnected = true;
        break;
      }
    }
    if (!disconnected) {
      throw new NoConnection();
    }
  }

  /**
   * Provides metadata about all connections of a specified receptacle.
   * 
   * @param receptacle The receptacle name.
   * @return An array with the connections metadata.
   * @throws InvalidName If the specified receptacle does not exist.
   * @see IReceptaclesOperations#getConnections(String)
   */
  public synchronized ConnectionDescription[] getConnections(String receptacle)
    throws InvalidName {
    Receptacle rec = myComponent.getReceptacleByName(receptacle);
    if (rec == null)
      throw new InvalidName();
    return rec.getDescription().connections;
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
