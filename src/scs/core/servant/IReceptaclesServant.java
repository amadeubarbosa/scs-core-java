package scs.core.servant;

import java.util.ArrayList;
import java.util.Map;

import scs.core.AlreadyConnected;
import scs.core.ConnectionDescription;
import scs.core.ExceededConnectionLimit;
import scs.core.IReceptacles;
import scs.core.IReceptaclesOperations;
import scs.core.IReceptaclesPOA;
import scs.core.InvalidConnection;
import scs.core.InvalidName;
import scs.core.NoConnection;

/**
 * Servant da interface IDL {@link IReceptacles}. Implementa as caracter�sticas
 * comuns a todos IReceptacles.
 */
public class IReceptaclesServant extends IReceptaclesPOA {

  /**
   * Contador para gerar o ID da conexao (por instancia)
   */
  private int connectionCounter = 0;

  /**
   * Limite de conex�es
   */
  private final int connectionLimit = 100;

  /**
   * Refer�ncia para o contexto do componente. O contexto prov�m facilidades
   * para acesso a dados compartilhados entre as diversas facetas
   */
  protected ComponentContext myComponent;

  /**
   * Construtor padr�o usado pela infra-estrutura do SCS durante a instancia��o
   * autom�tica das facetas
   * 
   * @param myComponent Contexto do componente contendo as descri��es das portas
   *        (facetas e recept�culos) e m�todos de ajuda que facilitam o uso da
   *        infra-estrutura
   */
  public IReceptaclesServant(ComponentContext myComponent) {
    this.myComponent = myComponent;
  }

  /**
   * Busca de recept�culo a partir de um nome fict�cio
   * 
   * @param name Nome que identifica o recept�culo
   * @return Refer�ncia para o recept�culo ou null
   */
  protected Receptacle findReceptacle(String name) {
    return myComponent.getReceptacles().get(name);
  }

  /**
   * Busca de recept�culo a partir do identificador da conex�o
   * 
   * @param connId Identificador da conex�o
   * @return Refer�ncia para o recept�culo ou null
   */
  protected Receptacle findReceptacleByConnection(int connId) {
    for (Receptacle rec : myComponent.getReceptacles().values()) {
      if (rec.getConnection(connId) != null)
        return rec;
    }

    return null;
  }

  /**
   * M�todo para conectar um objeto provedor de servi�o no recept�culo
   * 
   * @param receptacle Nome fict�cio do recept�culo
   * @param obj Refer�ncia para o objeto CORBA que implementa o servi�o
   * @return Inteiro identificador da conex�o
   * 
   * @see IReceptaclesOperations#connect(String, org.omg.CORBA.Object)
   */
  public int connect(String receptacle, org.omg.CORBA.Object obj)
    throws InvalidName, InvalidConnection, AlreadyConnected,
    ExceededConnectionLimit {

    Receptacle rec = this.findReceptacle(receptacle);
    if (rec == null)
      throw new InvalidName();

    if ((!rec.getReceptacleDescription().is_multiplex)
      && (!rec.getConnections().isEmpty()))
      throw new AlreadyConnected();

    if (this.connectionCounter >= this.connectionLimit)
      throw new ExceededConnectionLimit();

    if (!obj._is_a(rec.getInterfaceName()))
      throw new InvalidConnection();

    return rec.addConnection(++this.connectionCounter, obj);
  }

  /**
   * M�todo para desconectar um provedor de servi�o do recept�culo
   * 
   * @param id Inteiro identificador da conex�o
   */
  public void disconnect(int id) throws InvalidConnection, NoConnection {
    if (id < 0)
      throw new InvalidConnection();

    Receptacle rec = this.findReceptacleByConnection(id);
    if (rec == null)
      throw new NoConnection();

    rec.removeConnection(id);
  }

  /**
   * Listagem de descri��es da conex�es ativas
   * 
   * @param receptacle Nome fict�cio do recept�culo
   * @return Vetor com as descri��es das conex�es v�lidas para o recept�culo
   * @see IReceptaclesOperations#getConnections(String)
   */
  public ConnectionDescription[] getConnections(String receptacle)
    throws InvalidName {

    Receptacle rec = myComponent.getReceptacles().get(receptacle);
    if (rec == null)
      throw new InvalidName();
    return rec.getReceptacleDescription().connections;
  }

  public Map<String, Receptacle> getReceptacles() {
    return myComponent.getReceptacles();
  }

  /**
   * Retorna a refer�ncia para a faceta IComponent. Espec�fico do JACORB.
   */
  @Override
  public org.omg.CORBA.Object _get_component() {
    return this.myComponent.getIComponent();
  }
}
