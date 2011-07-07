/*
 * $Id$
 */
package scs.core.exception;

/**
 * 
 * @author Tecgraf/PUC-Rio
 */
public final class FacetDoesNotExist extends SCSException {
  public FacetDoesNotExist(String facet) {
    super(String.format("A faceta %s não existe", facet));
  }
}
