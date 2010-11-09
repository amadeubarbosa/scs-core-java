package scs.demos.stockmarket.servant;

import org.omg.CORBA.Object;

import scs.demos.stockmarket.util.StockSellerContext;
import StockMarket.StockServerPOA;

/**
 * @author augusto
 * 
 */
public class StockServerImpl extends StockServerPOA {
  private StockSellerContext context;

  /**
   * @param context
   */
  public StockServerImpl(StockSellerContext context) {
    this.context = context;
  }

  public String[] getStockSymbols() {
    return context.getWallet().getStockSymbols();
  }

  public float getStockValue(String symbol) {
    return context.getWallet().getStockValue(symbol);
  }

  @Override
  public Object _get_component() {
    return context.getIComponent();
  }
}
