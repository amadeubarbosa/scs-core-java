package scs.core.servant;

import java.util.Map;

import scs.core.ComponentId;
import scs.core.FacetDescription;
import scs.core.ReceptacleDescription;

/**
 * Interface que é implementada na infra-estrutura do SCS que disponibiliza
 * funcionalidades básicas como: (i) facilitar ao objeto utilitário do Builder;
 * (ii) retornar a faceta IComponent; (iii) acesso às descrições de facetas e
 * receptáculos
 * 
 * @author Carlos Eduardo
 * @author (comentários) Amadeu A. Barbosa Júnior
 * 
 */
public interface ComponentContext {

  public abstract ComponentId getComponentId();

  /**
   * Obtém o mapa de descrições das facetas conforme {@link FacetDescription}
   */
  public abstract Map<String, FacetDescription> getFacetDescs();

  /**
   * Obtém o mapa de descrições das facetas estendidas com o nome da classe Java que as
   * implementa
   */
  public abstract Map<String, ExtendedFacetDescription> getExtendedFacetDescs();

  /**
   * Obtém o mapa de descrições dos receptáculos
   */
  public abstract Map<String, ReceptacleDescription> getReceptacleDescs();

  /**
   * Obtém o mapa dos objetos Java das facetas
   */
  public abstract Map<String, Object> getFacets();

  /**
   * Obtém o mapa dos objetos Java dos receptáculos
   */
  public abstract Map<String, Receptacle> getReceptacles();

  /**
   * Obtém a referência para a classe utilitária do SCS
   */
  public abstract ComponentBuilder getBuilder();

  /**
   * Obtém a referência para o objeto CORBA do IComponent desse componente
   */
  public abstract org.omg.CORBA.Object getIComponent();

}
