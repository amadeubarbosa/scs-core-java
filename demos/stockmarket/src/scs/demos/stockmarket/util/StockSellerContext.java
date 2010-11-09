package scs.demos.stockmarket.util;

import java.io.IOException;

import scs.core.ComponentId;
import scs.core.servant.ComponentBuilder;
import scs.core.servant.ComponentContextImpl;

/**
 * @author augusto
 * 
 */
public class StockSellerContext extends ComponentContextImpl {
  // Ações com seus respectivos valores
  private StockWallet wallet;

  /**
   * @param builder
   * @param id
   * @throws IOException
   */
  public StockSellerContext(ComponentBuilder builder, ComponentId id)
    throws IOException {
    super(builder, id);
    wallet = new StockWallet();
  }

  /**
   * @return The stock map.
   */
  public StockWallet getWallet() {
    return wallet;
  }
}
