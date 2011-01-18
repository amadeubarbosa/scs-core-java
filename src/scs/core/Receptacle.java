package scs.core;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Classe criada para facilitar o gerenciamento dos receptáculos no servant da
 * faceta {@link IReceptacles}.
 * 
 */
public class Receptacle {
  private final String name;
  private final String interface_name;
  private final boolean is_multiplex;

  private ComponentContext myComponent;

  /**
   * Vetor de conexões, implementado como Vector para evitar cópia de memória ao
   * retornar como ConnectionDescription[] (mapeamento padrão de sequence
   * CORBA).
   */
  private List<ConnectionDescription> connections;

  /**
   * Construtor a partir de uma descrição.
   * 
   * @param name
   * @param interface_name
   * @param is_multiplex
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

  public String getName() {
    return name;
  }

  public String getInterfaceName() {
    return interface_name;
  }

  public boolean isMultiplex() {
    return is_multiplex;
  }

  /**
   * @return
   */
  public synchronized ReceptacleDescription getDescription() {
    return new ReceptacleDescription(name, interface_name, is_multiplex,
      connections.toArray(new ConnectionDescription[0]));
  }

  public synchronized List<ConnectionDescription> getConnections() {
    return Collections.unmodifiableList(connections);
  }

  public synchronized int getConnectionsSize() {
    return connections.size();
  }

  public synchronized void clearConnections() {
    connections.clear();
  }

  /**
   * Adiciona uma nova conexão a um proxy CORBA
   * 
   * @param id Identificador da conexão
   * @param obj Objeto CORBA do provedor de serviço
   * @throws InvalidName
   * @throws AlreadyConnected
   * @throws ExceededConnectionLimit
   * @throws InvalidConnection
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
   * Remove uma conexão pelo seu identificador
   * 
   * @param id Identificador da conexão
   * @throws InvalidConnection
   * @throws NoConnection
   */
  public synchronized void removeConnection(ConnectionDescription desc)
    throws NoConnection {
    if (!connections.remove(desc)) {
      throw new NoConnection();
    }
  }
}
