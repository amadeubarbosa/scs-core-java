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
      // inicializa��o do ORB
      Properties props = new Properties();
      // porta e host apenas para fins do exemplo
      props.put("org.omg.CORBA.ORBInitialPort", "1051");
      props.put("org.omg.CORBA.ORBInitialHost", "localhost");

      ORB orb = ORB.init(args, props);

      POA poa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
      poa.the_POAManager().activate();

      // assume-se que o arquivo que cont�m o IOR (publicado na listagem
      // anterior) esteja dispon�vel. O arquivo pode ter sido criado em 
      // outra m�quina e, nesse caso, tem de ser copiado manualmente
      // (pode-se tamb�m utilizar um m�todo diferente de publica��o,
      // como um servi�o de nomes).
      BufferedReader in = new BufferedReader(new FileReader("hello.ior"));
      String icIOR = in.readLine();

      // obten��o das facetas Hello e IComponent
      // precisamos utilizar o m�todo narrow pois estamos recebendo um
      // org.omg.CORBA.Object
      IComponent icFacet = IComponentHelper.narrow(orb.string_to_object(icIOR));
      Hello iHelloFacet =
        HelloHelper.narrow(icFacet.getFacet(HelloHelper.id()));

      // inicializa��o do componente.
      icFacet.startup();

      // com o componente inicializado, podemos utiliz�-lo � vontade.
      // note que o m�todo setName da classe HelloServant n�o pode ser utilizado
      // remotamente, pois n�o est� definido em IDL.
      iHelloFacet.sayHello();
    }
    catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
