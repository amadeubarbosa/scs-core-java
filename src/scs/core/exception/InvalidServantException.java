package scs.core.exception;

import org.omg.CORBA.UserException;

public class InvalidServantException extends SCSException {

  public InvalidServantException() {
  }

  public InvalidServantException(UserException cause) {
    super(cause);
  }

  public InvalidServantException(Throwable cause) {
    super(cause);
  }
}
