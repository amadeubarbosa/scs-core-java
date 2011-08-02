package scs.core;

import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;

import scs.core.exception.SCSException;

public final class MockComponentContext extends ComponentContext {
  public MockComponentContext(ORB orb, POA poa, ComponentId id)
    throws SCSException {
    super(orb, poa, id);
  }
}
