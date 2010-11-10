package scs.demos.stockmarket.servant;

import org.omg.CORBA.Object;

import scs.core.servant.ComponentContext;
import StockMarket.ExchangePrinterPOA;

/**
 * This class implements an ExchangePrinter facet that prints stock exchange
 * information on the standard output.
 * 
 * @author augusto
 */
public class DisplayExchangePrinterImpl extends ExchangePrinterPOA {
  private ComponentContext context;

  /**
   * Constructor.
   * 
   * @param context The component that this facet belongs to.
   */
  public DisplayExchangePrinterImpl(ComponentContext context) {
    this.context = context;
  }

  public void print(String symbol) {
    System.out.println("Purchase of stock " + symbol + " executed.");
  }

  @Override
  public Object _get_component() {
    return context.getIComponent();
  }
}
