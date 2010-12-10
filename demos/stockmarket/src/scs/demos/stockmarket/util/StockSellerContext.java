package scs.demos.stockmarket.util;

import java.io.IOException;

import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;

import scs.core.ComponentContext;
import scs.core.ComponentId;
import scs.core.exception.SCSException;

/**
 * This class represents the StockSeller component locally.
 * 
 * @author augusto
 * 
 */
public class StockSellerContext extends ComponentContext {
  // Stocks with their respective values and quantities
  private StockWallet wallet;

  /**
   * Constructor.
   * 
   * @param builder A builder associated with the ORB and POA used to create the
   *        servants
   * @param id This component's ComponentId
   * @throws IOException
   * @throws SCSException
   */
  public StockSellerContext(ORB orb, POA poa, ComponentId id)
    throws IOException, SCSException {
    super(orb, poa, id);
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
