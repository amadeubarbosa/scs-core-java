package scs.core.exception;

public final class ReceptacleDoesNotExistException extends SCSException {
  public ReceptacleDoesNotExistException(String receptacle) {
    super(String.format("O recept�culo %s n�o existe", receptacle));
  }
}
