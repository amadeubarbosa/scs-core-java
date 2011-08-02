package scs.core.exception;

import org.omg.CORBA.UserException;

/**
 * This exception is the most basic SCS exception and other exceptions derive
 * from it.
 * 
 */
public class SCSException extends Exception {
  /**
   * Default constructor.
   */
  public SCSException(String message) {
    super(message);
  }

  /**
   * Constructor that accepts an UserException as a cause.
   * 
   * @param cause The UserException object.
   */
  public SCSException(UserException cause) {
    super(cause);
  }

  /**
   * Constructor that accepts a Throwable as a cause.
   * 
   * @param cause The Throwable object.
   */
  public SCSException(Throwable cause) {
    super(cause);
  }

  public SCSException(String message, Throwable cause) {
    super(message, cause);
  }
}
