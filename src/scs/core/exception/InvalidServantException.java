package scs.core.exception;

import org.omg.CORBA.UserException;

/**
 * This exception signalizes that an invalid servant has been received as a
 * parameter.
 * 
 */
public class InvalidServantException extends SCSException {

  /**
   * Default constructor.
   */
  public InvalidServantException() {
  }

  /**
   * Constructor that accepts an UserException as a cause.
   * 
   * @param cause The UserException object.
   */
  public InvalidServantException(UserException cause) {
    super(cause);
  }

  /**
   * Constructor that accepts a Throwable as a cause.
   * 
   * @param cause The Throwable object.
   */
  public InvalidServantException(Throwable cause) {
    super(cause);
  }
}
