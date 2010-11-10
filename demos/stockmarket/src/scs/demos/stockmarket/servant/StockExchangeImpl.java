package scs.demos.stockmarket.servant;

import java.util.ArrayList;
import java.util.Iterator;

import org.omg.CORBA.Object;

import scs.core.ConnectionDescription;
import scs.demos.stockmarket.util.StockSellerContext;
import StockMarket.ExchangePrinter;
import StockMarket.ExchangePrinterHelper;
import StockMarket.StockExchangePOA;

/**
 * This class implements a StockExchange facet that allows the purchase of an
 * available stock.
 * 
 * @author augusto
 * 
 */
public class StockExchangeImpl extends StockExchangePOA {
  private StockSellerContext context;

  /**
   * Constructor.
   * 
   * @param context The component that this facet belongs to.
   */
  public StockExchangeImpl(StockSellerContext context) {
    this.context = context;
  }

  public boolean buyStock(String symbol) {
    if (context.getWallet().sellStock(symbol)) {
      try {
        ArrayList<ConnectionDescription> conns =
          context.getReceptacles().get("ExchangePrinter").getConnections();
        synchronized (conns) {
          for (Iterator<ConnectionDescription> iterator = conns.iterator(); iterator
            .hasNext();) {
            ConnectionDescription conn = iterator.next();
            ExchangePrinter printer = ExchangePrinterHelper.narrow(conn.objref);
            printer.print(symbol);
          }
        }
      }
      catch (Exception e) {
        e.printStackTrace();
      }
      return true;
    }
    return false;
  }

  @Override
  public Object _get_component() {
    return context.getIComponent();
  }
}
