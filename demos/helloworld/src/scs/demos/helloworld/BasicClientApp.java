package scs.demos.helloworld;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Properties;

import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import scs.core.IComponent;
import scs.core.IComponentHelper;

public class BasicClientApp {

  public static void main(String[] args) {
    try {
      // inicialização do ORB
      Properties props = new Properties();
      // porta e host apenas para fins do exemplo
      props.put("org.omg.CORBA.ORBInitialPort", "1051");
      props.put("org.omg.CORBA.ORBInitialHost", "localhost");

      ORB orb = ORB.init(args, props);

      POA poa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
      poa.the_POAManager().activate();

      // assume-se que o arquivo que contém o IOR (publicado na listagem
      // anterior) esteja disponível. O arquivo pode ter sido criado em 
      // outra máquina e, nesse caso, tem de ser copiado manualmente
      // (pode-se também utilizar um método diferente de publicação,
      // como um serviço de nomes).
      BufferedReader in = new BufferedReader(new FileReader("hello.ior"));
      String icIOR = in.readLine();

      // obtenção das facetas Hello e IComponent
      // precisamos utilizar o método narrow pois estamos recebendo um
      // org.omg.CORBA.Object
      IComponent icFacet = IComponentHelper.narrow(orb.string_to_object(icIOR));
      Hello iHelloFacet =
        HelloHelper.narrow(icFacet.getFacet(HelloHelper.id()));

      // inicialização do componente.
      icFacet.startup();

      // com o componente inicializado, podemos utilizá-lo à vontade.
      // note que o método setName da classe HelloServant não pode ser utilizado
      // remotamente, pois não está definido em IDL.
      iHelloFacet.sayHello();
    }
    catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
