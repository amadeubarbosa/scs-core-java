package scs.core.servant;

import java.util.ArrayList;
import java.util.Iterator;

import scs.core.AlreadyConnected;
import scs.core.ConnectionDescription;
import scs.core.ExceededConnectionLimit;
import scs.core.IReceptacles;
import scs.core.ReceptacleDescription;

/**
 * Classe criada para facilitar o gerenciamento dos receptáculos no servant da
 * faceta {@link IReceptacles}.
 * 
 * @author Eduardo Fonseca
 * @author Luiz Marques
 * @author (comentários) Amadeu A. Barbosa Júnior
 */
public class Receptacle {
  /**
   * Descrição do receptáculo.
   */
  ReceptacleDescription desc;
  /**
   * Vetor de conexões, implementado como Vector para evitar cópia de memória ao
   * retornar como ConnectionDescription[] (mapeamento padrão de sequence
   * CORBA).
   */
  ArrayList<ConnectionDescription> connections;

  /**
   * Construtor a partir de uma descrição.
   * 
   * @param desc Descrição do receptáculo.
   */
  public Receptacle(ReceptacleDescription desc) {
    this.desc = desc;
    this.connections = new ArrayList<ConnectionDescription>();
  }

  public ReceptacleDescription getReceptacleDescription() {
    return desc;
  }

  public String getName() {
    return desc.name;
  }

  public String getInterfaceName() {
    return desc.interface_name;
  }

  public boolean isMultiplex() {
    return desc.is_multiplex;
  }

  public ArrayList<ConnectionDescription> getConnections() {
    return connections;
  }

  /**
   * Obtém o contador de conexões para esse receptáculo.
   * 
   * @return Tamanho da coleção de conexões.
   */
  public int getConnectionsCounter() {
    return connections.size();
  }

  /**
   * Obtém a descrição de uma conexão a partir de um identificador numérico.
   * 
   * @param id Identificador numérico
   * @return Descrição da conexão
   */
  public ConnectionDescription getConnection(int id) {
    for (ConnectionDescription conn : this.connections) {
      if (conn.id == id)
        return conn;
    }
    return null;
  }

  /**
   * Adiciona uma nova conexão a um proxy CORBA
   * 
   * @param id Identificador da conexão
   * @param obj Objeto CORBA do provedor de serviço
   * @return Identificador da conexão
   * @throws AlreadyConnected
   * @throws ExceededConnectionLimit
   */
  public int addConnection(int id, org.omg.CORBA.Object obj)
    throws AlreadyConnected {
    for (ConnectionDescription conn : this.connections) {
      if (conn.objref.equals(obj))
        throw new AlreadyConnected();
    }

    ConnectionDescription conn = new ConnectionDescription();
    conn.id = id;
    conn.objref = obj;
    this.connections.add(conn);

    // We need update the connections vector because the descriptions could be
    // read from another places, because of Base.getReceptacleDescs() has a
    // reference to ReceptacleDescription too and the
    // ReceptacleDescription.connections reference!
    this.desc.connections =
      this.connections.toArray(new ConnectionDescription[this.connections.size()]);

    return conn.id;
  }

  /**
   * Remove uma conexão pelo seu identificador
   * 
   * @param id Identificador da conexão
   */
  public void removeConnection(int id) {
    for (Iterator<ConnectionDescription> iter = this.connections.iterator(); iter
      .hasNext();) {
      ConnectionDescription conn = iter.next();
      if (conn.id == id) {
        iter.remove();
        // We need update the connections vector because the descriptions could be
        // read from another places, because of Base.getReceptacleDescs() has a
        // reference to ReceptacleDescription too and the
        // ReceptacleDescription.connections reference!
        this.desc.connections =
          this.connections.toArray(new ConnectionDescription[this.connections.size()]); 
        return;
      }
        
    }
  }
}
