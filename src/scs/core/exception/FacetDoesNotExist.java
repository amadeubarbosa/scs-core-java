/*
 * $Id$
 */
package scs.core.exception;

/**
 * 
 * @author Tecgraf/PUC-Rio
 */
public final class FacetDoesNotExist extends SCSException {
  private String facet;

  public FacetDoesNotExist(String facet) {
    this.facet = facet;
  }
}
