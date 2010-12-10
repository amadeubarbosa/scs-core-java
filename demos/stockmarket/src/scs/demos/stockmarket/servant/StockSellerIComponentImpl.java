package scs.demos.stockmarket.servant;

import scs.core.IComponentServant;
import scs.core.Receptacle;
import scs.core.StartupFailed;
import scs.demos.stockmarket.util.StockSellerContext;

/**
 * This class implements an IComponent facet for the StockSeller component. It
 * enforces necessary connections and remove them on shutdown.
 * 
 * @author augusto
 * 
 */
public class StockSellerIComponentImpl extends IComponentServant {

  /**
   * Constructor.
   * 
   * @param context The component that this facet belongs to.
   */
  public StockSellerIComponentImpl(StockSellerContext context) {
    super(context);
  }

  @Override
  public synchronized void startup() throws StartupFailed {
    if (myComponent.getReceptacleByName("ExchangePrinter").getConnections()
      .size() < 1) {
      throw new StartupFailed("There must be at least one printer connected.");
    }
  }

  @Override
  public synchronized void shutdown() {
    Receptacle rec = myComponent.getReceptacleByName("ExchangePrinter");
    rec.clearConnections();
  }
}
