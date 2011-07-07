package scs.core.exception;

public final class ReceptacleDoesNotExistException extends SCSException {
  public ReceptacleDoesNotExistException(String receptacle) {
    super(String.format("O receptáculo %s não existe", receptacle));
  }
}
