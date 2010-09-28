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
 * Servant da interface IDL {@link IReceptacles}. Implementa as características
 * comuns a todos IReceptacles.
 */
public class IReceptaclesServant extends IReceptaclesPOA {

  /**
   * Contador para gerar o ID da conexao (por instancia)
   */
  private int connectionCounter = 0;

  /**
   * Limite de conexões
   */
  private final int connectionLimit = 100;

  /**
   * Referência para o contexto do componente. O contexto provém facilidades
   * para acesso a dados compartilhados entre as diversas facetas
   */
  protected ComponentContext myComponent;

  /**
   * Construtor padrão usado pela infra-estrutura do SCS durante a instanciação
   * automática das facetas
   * 
   * @param myComponent Contexto do componente contendo as descrições das portas
   *        (facetas e receptáculos) e métodos de ajuda que facilitam o uso da
   *        infra-estrutura
   */
  public IReceptaclesServant(ComponentContext myComponent) {
    this.myComponent = myComponent;
  }

  /**
   * Busca de receptáculo a partir de um nome fictício
   * 
   * @param name Nome que identifica o receptáculo
   * @return Referência para o receptáculo ou null
   */
  protected Receptacle findReceptacle(String name) {
    return myComponent.getReceptacles().get(name);
  }

  /**
   * Busca de receptáculo a partir do identificador da conexão
   * 
   * @param connId Identificador da conexão
   * @return Referência para o receptáculo ou null
   */
  protected Receptacle findReceptacleByConnection(int connId) {
    for (Receptacle rec : myComponent.getReceptacles().values()) {
      if (rec.getConnection(connId) != null)
        return rec;
    }

    return null;
  }

  /**
   * Método para conectar um objeto provedor de serviço no receptáculo
   * 
   * @param receptacle Nome fictício do receptáculo
   * @param obj Referência para o objeto CORBA que implementa o serviço
   * @return Inteiro identificador da conexão
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
   * Método para desconectar um provedor de serviço do receptáculo
   * 
   * @param id Inteiro identificador da conexão
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
   * Listagem de descrições da conexões ativas
   * 
   * @param receptacle Nome fictício do receptáculo
   * @return Vetor com as descrições das conexões válidas para o receptáculo
   * @see IReceptaclesOperations#getConnections(String)
   */
  public ConnectionDescription[] getConnections(String receptacle)
    throws InvalidName {

    Receptacle rec = myComponent.getReceptacles().get(receptacle);
    if (rec == null)
      throw new InvalidName();
    ArrayList<ConnectionDescription> conns = rec.getConnections();
    return conns.toArray(new ConnectionDescription[conns.size()]);
  }

  public Map<String, Receptacle> getReceptacles() {
    return myComponent.getReceptacles();
  }

  /**
   * Retorna a referência para a faceta IComponent. Específico do JACORB.
   */
  @Override
  public org.omg.CORBA.Object _get_component() {
    return this.myComponent.getIComponent();
  }
}
