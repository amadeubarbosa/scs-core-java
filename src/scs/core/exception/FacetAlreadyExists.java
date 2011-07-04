/*
 * $Id$
 */
package scs.core.exception;

/**
 * 
 * @author Tecgraf/PUC-Rio
 */
public final class FacetAlreadyExists extends SCSException {
  private String facet;

  public FacetAlreadyExists(String facet) {
    this.facet = facet;
  }
}
