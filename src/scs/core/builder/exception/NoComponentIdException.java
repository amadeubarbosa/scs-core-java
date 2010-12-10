package scs.core.builder.exception;

import org.omg.CORBA.UserException;

import scs.core.exception.SCSException;

public class NoComponentIdException extends SCSException {

  public NoComponentIdException() {
  }

  public NoComponentIdException(UserException cause) {
    super(cause);
  }

  public NoComponentIdException(Throwable cause) {
    super(cause);
  }
}
