package scs.core;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This class holds all the metadata pertinent to a receptacle, and represents
 * it locally.
 * 
 * It doesn't maintain a ReceptacleDescription directly because the
 * ReceptacleDescription is an automatically generated class. The generated code
 * sets all of its fields as public but these fields should not be manipulated
 * freely.
 */
public class Receptacle {
  /**
   * The name of the receptacle, which acts as its identifier within the
   * component.
   */
  private final String name;

  /**
   * The IDL interface that this receptacle represents.
   */
  private final String interface_name;

  /**
   * Indicates if the receptacle accepts only one connection or multiple.
   */
  private final boolean is_multiplex;

  /**
   * Reference to the context of this receptacle, i.e., the local representation
   * of its component.
   */
  private ComponentContext myComponent;

  /**
   * List of connections.
   */
  private List<ConnectionDescription> connections;

  /**
   * Primary constructor.
   * 
   * @param context Reference to the context of this receptacle, i.e., the local
   *        representation of its component.
   * @param name The receptacle name.
   * @param interface_name The interface that facets must implement to be
   *        connected to this receptacle.
   * @param is_multiplex Indicates if the receptacle accepts more than one
   *        connection.
   * 
   */
  Receptacle(ComponentContext context, String name, String interface_name,
    boolean is_multiplex) {
    if (context == null) {
      throw new IllegalArgumentException(
        "The component's context can't be null");
    }
    if (name == null) {
      throw new IllegalArgumentException("The name can't be null");
    }
    if (interface_name == null) {
      throw new IllegalArgumentException("The interface's name can't be null");
    }

    this.myComponent = context;
    this.name = name;
    this.interface_name = interface_name;
    this.is_multiplex = is_multiplex;
    this.connections = new LinkedList<ConnectionDescription>();
  }

  /**
   * Provides the name of this receptacle.
   * 
   * @return The receptacle name.
   */
  public String getName() {
    return name;
  }

  /**
   * Provides the interface that this receptacle expects. Remote facets must
   * implement this interface to be connected to this receptacle.
   * 
   * @return The receptacle interface.
   */
  public String getInterfaceName() {
    return interface_name;
  }

  /**
   * Indicates whether this receptacle accepts more than one connection.
   * 
   * @return True if the receptacle accepts more than one connection, false
   *         otherwise.
   */
  public boolean isMultiplex() {
    return is_multiplex;
  }

  /**
   * Provides the IDL structure ReceptacleDescription of this receptacle.
   * 
   * @return ReceptacleDescription The description.
   */
  public synchronized ReceptacleDescription getDescription() {
    return new ReceptacleDescription(name, interface_name, is_multiplex,
      connections.toArray(new ConnectionDescription[0]));
  }

  /**
   * Provides a list with metadata about all active connections to this
   * receptacle.
   * 
   * @return List with the connections metadata.
   */
  public synchronized List<ConnectionDescription> getConnections() {
    return Collections.unmodifiableList(connections);
  }

  /**
   * Provides the current number of connections.
   * 
   * @return The current number of connections.
   */
  public synchronized int getConnectionsSize() {
    return connections.size();
  }

  /**
   * Removes all connections.
   */
  public synchronized void clearConnections() {
    connections.clear();
  }

  /**
   * Connects a remote facet to this receptacle. The facet must implement the
   * interface specified.
   * 
   * @param obj The remote facet reference.
   * @return The connection identifier. It's valid for the entire component, not
   *         for this specific receptacle.
   * @throws InvalidConnection If the facet object does not implement this
   *         receptacle's specified interface.
   * @throws AlreadyConnected If this receptacle supports only one connection
   *         and is already connected.
   * @throws ExceededConnectionLimit If the receptacle is multiplex and the
   *         maximum number of connections was already reached.
   */
  public synchronized int addConnection(org.omg.CORBA.Object obj)
    throws AlreadyConnected, ExceededConnectionLimit, InvalidConnection {
    if (obj == null) {
      throw new IllegalArgumentException("The connection can't be null");
    }

    if ((!is_multiplex) && (getConnectionsSize() > 0)) {
      throw new AlreadyConnected();
    }

    int numConnections = 0;
    for (Receptacle receptacle : myComponent.getReceptacles()) {
      numConnections += receptacle.getConnectionsSize();
    }
    if (numConnections >= myComponent.CONNECTION_LIMIT) {
      throw new ExceededConnectionLimit();
    }

    if (!obj._is_a(interface_name))
      throw new InvalidConnection();

    int newId = myComponent.generateConnectionId();
    this.connections.add(new ConnectionDescription(newId, obj));

    return newId;
  }

  /**
   * Disconnects a remote facet from this receptacle.
   * 
   * @param id The connection identifier.
   * @return True if the connection was removed, false otherwise.
   */
  public synchronized boolean removeConnection(int id) {
    if (id <= 0) {
      throw new IllegalArgumentException(
        "The connection's id can't be less than zero");
    }
    int connectionIndex = -1;
    for (int i = 0; i < this.connections.size(); i++) {
      ConnectionDescription connection = this.connections.get(i);
      if (connection.id == id) {
        connectionIndex = i;
        break;
      }
    }
    if (connectionIndex == -1) {
      return false;
    }
    this.connections.remove(connectionIndex);
    return true;
  }
}
