package scs.core.servant;

import java.util.ArrayList;
import java.util.Iterator;

import scs.core.AlreadyConnected;
import scs.core.ConnectionDescription;
import scs.core.ExceededConnectionLimit;
import scs.core.IReceptacles;
import scs.core.ReceptacleDescription;

/**
 * Classe criada para facilitar o gerenciamento dos recept�culos no servant da
 * faceta {@link IReceptacles}.
 * 
 * @author Eduardo Fonseca
 * @author Luiz Marques
 * @author (coment�rios) Amadeu A. Barbosa J�nior
 */
public class Receptacle {
  /**
   * Descri��o do recept�culo.
   */
  ReceptacleDescription desc;
  /**
   * Vetor de conex�es, implementado como Vector para evitar c�pia de mem�ria ao
   * retornar como ConnectionDescription[] (mapeamento padr�o de sequence
   * CORBA).
   */
  ArrayList<ConnectionDescription> connections;

  /**
   * Construtor a partir de uma descri��o.
   * 
   * @param desc Descri��o do recept�culo.
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
   * Obt�m o contador de conex�es para esse recept�culo.
   * 
   * @return Tamanho da cole��o de conex�es.
   */
  public int getConnectionsCounter() {
    return connections.size();
  }

  /**
   * Obt�m a descri��o de uma conex�o a partir de um identificador num�rico.
   * 
   * @param id Identificador num�rico
   * @return Descri��o da conex�o
   */
  public ConnectionDescription getConnection(int id) {
    for (ConnectionDescription conn : this.connections) {
      if (conn.id == id)
        return conn;
    }
    return null;
  }

  /**
   * Adiciona uma nova conex�o a um proxy CORBA
   * 
   * @param id Identificador da conex�o
   * @param obj Objeto CORBA do provedor de servi�o
   * @return Identificador da conex�o
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
   * Remove uma conex�o pelo seu identificador
   * 
   * @param id Identificador da conex�o
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
