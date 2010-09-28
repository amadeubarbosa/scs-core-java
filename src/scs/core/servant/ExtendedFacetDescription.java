package scs.core.servant;

/**
 * Classe que implementa a estrutura da {@link FacetDescription} e estende com
 * o nome da classe Java que implementa essa faceta.
 * 
 * @author Carlos Eduardo
 * @author (comentários) Amadeu A. Barbosa Júnior
 * 
 */
public class ExtendedFacetDescription {
  /** Nome da faceta */
  public String name;
  /** Interface IDL para a faceta */
  public String interface_name;
  /** Classe Java que implementa a faceta */
  public String class_name;

  public ExtendedFacetDescription(String name, String interface_name,
    String class_name) {
    this.name = name;
    this.interface_name = interface_name;
    this.class_name = class_name;
  }
}
