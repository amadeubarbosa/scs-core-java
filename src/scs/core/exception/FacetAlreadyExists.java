/*
 * $Id$
 */
package scs.core.exception;

/**
 * 
 * @author Tecgraf/PUC-Rio
 */
public final class FacetAlreadyExists extends SCSException {
  public FacetAlreadyExists(String facet) {
    super(String.format("A faceta %s já existe", facet));
  }
}
