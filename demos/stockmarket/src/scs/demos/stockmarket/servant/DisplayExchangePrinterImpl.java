package scs.demos.stockmarket.servant;

import org.omg.CORBA.Object;

import scs.core.servant.ComponentContext;
import StockMarket.ExchangePrinterPOA;

/**
 * @author augusto
 * 
 */
public class DisplayExchangePrinterImpl extends ExchangePrinterPOA {
  private ComponentContext context;

  /**
   * @param context
   */
  public DisplayExchangePrinterImpl(ComponentContext context) {
    this.context = context;
  }

  public void print(String symbol) {
    System.out.println("Compra da ação " + symbol + " executada.");
  }

  @Override
  public Object _get_component() {
    return context.getIComponent();
  }
}
