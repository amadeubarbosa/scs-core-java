package scs.demos.helloworld;

import java.io.FileWriter;
import java.util.Properties;

import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import scs.core.ComponentId;
import scs.core.ReceptacleDescription;
import scs.core.servant.ComponentBuilder;
import scs.core.servant.ComponentContext;
import scs.core.servant.ExtendedFacetDescription;
import scs.demos.helloworld.servant.HelloServant;

public class BasicServerApp {

  // Facet names
  public static final String FACET_HELLO = Hello.class.getSimpleName();

  public static void main(String[] args) {
    try {
      // inicialização do ORB
      Properties props = new Properties();
      // porta e host apenas para fins do exemplo
      props.put("org.omg.CORBA.ORBInitialPort", "1050");
      props.put("org.omg.CORBA.ORBInitialHost", "localhost");

      ORB orb = ORB.init(args, props);

      POA poa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
      poa.the_POAManager().activate();

      // criação de descrições de facetas e receptáculos
      ExtendedFacetDescription[] extDescs = new ExtendedFacetDescription[1];
      ReceptacleDescription[] recepDescs = new ReceptacleDescription[0];
      extDescs[0] =
        new ExtendedFacetDescription("Hello",
          "IDL:scs/demos/helloworld/Hello:1.0",
          "scs.demos.helloworld.servant.HelloServant");

      // criação do ComponentId
      ComponentId cpId =
        new ComponentId("Hello", (byte) 1, (byte) 0, (byte) 0, "none");

      // criação do Construtor de Componentes e instanciação do componente
      ComponentBuilder builder = new ComponentBuilder(poa, orb);
      ComponentContext instance =
        builder.newComponent(extDescs, recepDescs, cpId);

      // modificação do nome a ser exibido na mensagem da faceta Hello
      HelloServant helloImpl =
        (HelloServant) instance.getFacets().get(FACET_HELLO);
      helloImpl.setName("User");

      // publicação do IOR para que a faceta Hello do componente possa ser 
      // encontrada. Observação: podemos exportar qualquer faceta, pois temos 
      // o método _get_component para obter a faceta IComponent e, com ela, 
      // pode-se obter outras facetas(esse passo pode ser substituído por outras 
      // formas de publicação, como a publicação em um serviço de nomes, por
      // exemplo).
      org.omg.CORBA.Object helloObj =
        instance.getFacetDescs().get("Hello").facet_ref;
      String helloIOR = orb.object_to_string(helloObj);
      FileWriter file = new FileWriter("hello.ior");
      file.write(helloIOR);
      file.flush();
      file.close();

      // instrução ao ORB para que aguarde por chamadas remotas
      orb.run();
    }
    catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
