package scs.core.servant;

import org.omg.CORBA.ORB;

/**
 * Classe utilitaria que cria uma thread para a execucao do {@link ORB} sem bloquear a
 * thread principal da aplicacao
 * 
 * @author Eduardo Fonseca e Luiz Marques
 * 
 */
public class OrbRunner extends Thread {
  ORB orb;

  public OrbRunner(ORB orb) {
    this.orb = orb;
  }

  @Override
  public void run() {
    this.orb.run();
  }

}
