package scs.core;

import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;

import scs.core.exception.SCSException;

public final class MockComponentContextInvalidConstructor extends
  ComponentContext {
  public MockComponentContextInvalidConstructor(ORB orb, POA poa,
    ComponentId id, String invalid) throws SCSException {
    super(orb, poa, id);
  }
}
