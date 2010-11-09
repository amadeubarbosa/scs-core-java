package scs.demos.stockmarket.servant;

import java.util.ArrayList;

import scs.core.ConnectionDescription;
import scs.core.StartupFailed;
import scs.core.servant.IComponentServant;
import scs.core.servant.Receptacle;
import scs.demos.stockmarket.util.StockSellerContext;

/**
 * @author augusto
 * 
 */
public class StockSellerIComponentImpl extends IComponentServant {

  /**
   * @param context
   */
  public StockSellerIComponentImpl(StockSellerContext context) {
    super(context);
  }

  @Override
  public void startup() throws StartupFailed {
    if (myComponent.getReceptacles().get("ExchangePrinter").getConnections()
      .size() < 1) {
      throw new StartupFailed("There must be at least one printer connected.");
    }
  }

  @Override
  public void shutdown() {
    Receptacle rec = myComponent.getReceptacles().get("ExchangePrinter");
    ArrayList<ConnectionDescription> conns = rec.getConnections();
    synchronized (conns) {
      conns.clear();
    }
  }
}
