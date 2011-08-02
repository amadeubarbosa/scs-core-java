package scs.core.builder.exception;

import scs.core.exception.SCSException;

public final class SchemaNotFoundException extends SCSException {
  public SchemaNotFoundException(String message) {
    super(message);
  }
}
