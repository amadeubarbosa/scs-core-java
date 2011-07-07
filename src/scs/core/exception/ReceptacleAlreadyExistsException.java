package scs.core.exception;

public final class ReceptacleAlreadyExistsException extends SCSException {
  public ReceptacleAlreadyExistsException(String receptacle) {
    super(String.format("O recept�culo %s j� existe", receptacle));
  }
}
