package scs.demos.helloworld;

import java.io.FileWriter;
import java.util.Properties;

import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import scs.core.ComponentContext;
import scs.core.ComponentId;
import scs.demos.helloworld.servant.HelloServant;

public class BasicServerApp {

  // Facet names
  public static final String FACET_HELLO = Hello.class.getSimpleName();

  public static void main(String[] args) {
    try {
      // inicializa��o do ORB
      Properties props = new Properties();
      // porta e host apenas para fins do exemplo
      props.put("org.omg.CORBA.ORBInitialPort", "1050");
      props.put("org.omg.CORBA.ORBInitialHost", "localhost");

      ORB orb = ORB.init(args, props);

      POA poa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
      poa.the_POAManager().activate();

      // cria��o do ComponentId
      ComponentId cpId =
        new ComponentId("Hello", (byte) 1, (byte) 0, (byte) 0, "none");

      // cria��o do componente
      ComponentContext component = new ComponentContext(orb, poa, cpId);

      // adi��o das facetas
      component.putFacet(FACET_HELLO, HelloHelper.id(), new HelloServant(
        component));

      // modifica��o do nome a ser exibido na mensagem da faceta Hello
      HelloServant helloImpl =
        (HelloServant) component.getFacetByName(FACET_HELLO).getServant();
      helloImpl.setName("User");

      // publica��o do IOR do componente.
      org.omg.CORBA.Object icObj = component.getIComponent();
      String helloIOR = orb.object_to_string(icObj);
      FileWriter file = new FileWriter("hello.ior");
      file.write(helloIOR);
      file.flush();
      file.close();

      // instru��o ao ORB para que aguarde por chamadas remotas
      orb.run();
    }
    catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
