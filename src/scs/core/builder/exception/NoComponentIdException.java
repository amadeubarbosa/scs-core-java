package scs.core.builder.exception;

import org.omg.CORBA.UserException;

import scs.core.exception.SCSException;

/**
 * This exception signalizes that a component cannot be assembled because its
 * ComponentId is missing and must be provided.
 * 
 */
public class NoComponentIdException extends SCSException {

  /**
   * Default constructor.
   */
  public NoComponentIdException() {
  }

  /**
   * Constructor that accepts an UserException as a cause.
   * 
   * @param cause The UserException object.
   */
  public NoComponentIdException(UserException cause) {
    super(cause);
  }

  /**
   * Constructor that accepts a Throwable as a cause.
   * 
   * @param cause The Throwable object.
   */
  public NoComponentIdException(Throwable cause) {
    super(cause);
  }
}
