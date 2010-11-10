package scs.demos.stockmarket.util;

import java.io.IOException;

import scs.core.ComponentId;
import scs.core.servant.ComponentBuilder;
import scs.core.servant.ComponentContextImpl;

/**
 * This class represents the StockSeller component locally.
 * 
 * @author augusto
 * 
 */
public class StockSellerContext extends ComponentContextImpl {
  // Stocks with their respective values and quantities
  private StockWallet wallet;

  /**
   * Constructor.
   * 
   * @param builder A builder associated with the ORB and POA used to create the
   *        servants
   * @param id This component's ComponentId
   * @throws IOException
   */
  public StockSellerContext(ComponentBuilder builder, ComponentId id)
    throws IOException {
    super(builder, id);
    wallet = new StockWallet();
  }

  /**
   * Provides the stock wallet.
   * 
   * @return The stock wallet.
   */
  public StockWallet getWallet() {
    return wallet;
  }
}
