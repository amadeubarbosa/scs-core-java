package scs.core;

/**
 * Servant da interface IDL {@link IReceptacles}. Implementa as características
 * comuns a todos IReceptacles.
 */
public class IReceptaclesServant extends IReceptaclesPOA {

  /**
   * Referência para o contexto do componente. O contexto provém facilidades
   * para acesso a dados compartilhados entre as diversas facetas
   */
  private ComponentContext myComponent;

  /**
   * Construtor padrão que recebe o contexto do componente.
   * 
   * @param myComponent Contexto do componente contendo as descrições das portas
   *        (facetas e receptáculos) e métodos de ajuda que facilitam o uso da
   *        infra-estrutura
   */
  public IReceptaclesServant(ComponentContext myComponent) {
    this.myComponent = myComponent;
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
  public synchronized int connect(String receptacle, org.omg.CORBA.Object obj)
    throws InvalidName, InvalidConnection, AlreadyConnected,
    ExceededConnectionLimit {
    Receptacle rec = myComponent.getReceptacleByName(receptacle);
    if (rec == null)
      throw new InvalidName();

    return rec.addConnection(obj);
  }

  /**
   * Método para desconectar um provedor de serviço do receptáculo
   * 
   * @param id Inteiro identificador da conexão
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
   * Listagem de descrições da conexões ativas
   * 
   * @param receptacle Nome fictício do receptáculo
   * @return Vetor com as descrições das conexões válidas para o receptáculo
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
   * Retorna a referência para a faceta IComponent. Específico do JACORB.
   */
  @Override
  public org.omg.CORBA.Object _get_component() {
    return this.myComponent.getIComponent();
  }
}
