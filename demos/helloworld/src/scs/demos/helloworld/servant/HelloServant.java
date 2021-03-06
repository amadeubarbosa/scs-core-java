package scs.demos.helloworld.servant;

import scs.core.ComponentContext;
import scs.demos.helloworld.HelloPOA;

public class HelloServant extends HelloPOA {

  private String name = "World";
  private ComponentContext myComponent;

  public HelloServant(ComponentContext myComponent) {
    super();
    this.myComponent = myComponent;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void sayHello() {
    System.out.println("Hello " + name + "!");
  }

  @Override
  public org.omg.CORBA.Object _get_component() {
    return myComponent.getIComponent();
  }
}
