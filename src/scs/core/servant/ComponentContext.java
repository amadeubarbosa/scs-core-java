package scs.core.servant;

import java.util.Map;

import scs.core.ComponentId;
import scs.core.FacetDescription;
import scs.core.ReceptacleDescription;

/**
 * Interface que � implementada na infra-estrutura do SCS que disponibiliza
 * funcionalidades b�sicas como: (i) facilitar ao objeto utilit�rio do Builder;
 * (ii) retornar a faceta IComponent; (iii) acesso �s descri��es de facetas e
 * recept�culos
 * 
 * @author Carlos Eduardo
 * @author (coment�rios) Amadeu A. Barbosa J�nior
 * 
 */
public interface ComponentContext {

  public abstract ComponentId getComponentId();

  /**
   * Obt�m o mapa de descri��es das facetas conforme {@link FacetDescription}
   */
  public abstract Map<String, FacetDescription> getFacetDescs();

  /**
   * Obt�m o mapa de descri��es das facetas estendidas com o nome da classe Java que as
   * implementa
   */
  public abstract Map<String, ExtendedFacetDescription> getExtendedFacetDescs();

  /**
   * Obt�m o mapa de descri��es dos recept�culos
   */
  public abstract Map<String, ReceptacleDescription> getReceptacleDescs();

  /**
   * Obt�m o mapa dos objetos Java das facetas
   */
  public abstract Map<String, Object> getFacets();

  /**
   * Obt�m o mapa dos objetos Java dos recept�culos
   */
  public abstract Map<String, Receptacle> getReceptacles();

  /**
   * Obt�m a refer�ncia para a classe utilit�ria do SCS
   */
  public abstract ComponentBuilder getBuilder();

  /**
   * Obt�m a refer�ncia para o objeto CORBA do IComponent desse componente
   */
  public abstract org.omg.CORBA.Object getIComponent();

}
