package scs.core.exception;

import org.omg.CORBA.UserException;

public class SCSException extends Exception {

  public SCSException() {
  }

  public SCSException(UserException cause) {
    super(cause);
  }

  public SCSException(Throwable cause) {
    super(cause);
  }
}
