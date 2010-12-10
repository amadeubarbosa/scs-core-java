package scs.core;

/**
 * Servant da interface IDL {@link IReceptacles}. Implementa as caracter�sticas
 * comuns a todos IReceptacles.
 */
public class IReceptaclesServant extends IReceptaclesPOA {

  /**
   * Refer�ncia para o contexto do componente. O contexto prov�m facilidades
   * para acesso a dados compartilhados entre as diversas facetas
   */
  protected ComponentContext myComponent;

  /**
   * Construtor padr�o que recebe o contexto do componente.
   * 
   * @param myComponent Contexto do componente contendo as descri��es das portas
   *        (facetas e recept�culos) e m�todos de ajuda que facilitam o uso da
   *        infra-estrutura
   */
  public IReceptaclesServant(ComponentContext myComponent) {
    this.myComponent = myComponent;
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
  public synchronized int connect(String receptacle, org.omg.CORBA.Object obj)
    throws InvalidName, InvalidConnection, AlreadyConnected,
    ExceededConnectionLimit {
    Receptacle rec = myComponent.getReceptacleByName(receptacle);
    if (rec == null)
      throw new InvalidName();

    return rec.addConnection(obj);
  }

  /**
   * M�todo para desconectar um provedor de servi�o do recept�culo
   * 
   * @param id Inteiro identificador da conex�o
   */
  public synchronized void disconnect(int id) throws InvalidConnection,
    NoConnection {
    if (id < 0) {
      throw new InvalidConnection();
    }

    ConnectionDescription connDesc = null;
    for (Receptacle receptacle : myComponent.getReceptacles()) {
      for (ConnectionDescription desc : receptacle.getConnections()) {
        if (desc.id == id) {
          connDesc = desc;
          break;
        }
      }
      if (connDesc != null) {
        receptacle.removeConnection(connDesc);
        return;
      }
    }
    throw new NoConnection();
  }

  /**
   * Listagem de descri��es da conex�es ativas
   * 
   * @param receptacle Nome fict�cio do recept�culo
   * @return Vetor com as descri��es das conex�es v�lidas para o recept�culo
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
   * Retorna a refer�ncia para a faceta IComponent. Espec�fico do JACORB.
   */
  @Override
  public org.omg.CORBA.Object _get_component() {
    return this.myComponent.getIComponent();
  }
}
