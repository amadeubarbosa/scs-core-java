package scs.demos.stockmarket.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * This class represents a stock wallet. It contains stocks with associated
 * values and quantities.
 * 
 * @author augusto
 * 
 */
public class StockWallet {
  private Properties values;
  private Properties quantities;

  /**
   * Default constructor.
   * 
   * @throws IOException
   * 
   */
  public StockWallet() throws IOException {
    values = new Properties();
    quantities = new Properties();
    values.load(new FileInputStream("stocks.prices"));
    quantities.load(new FileInputStream("stocks.quantities"));
  }

  /**
   * Sells one unit of a specific stock.
   * 
   * @param symbol The stock being sold.
   * @return True if the symbol was present and positive in quantity. False if
   *         not.
   */
  synchronized public boolean sellStock(String symbol) {
    if (quantities.containsKey(symbol)) {
      int quantity = Integer.parseInt(quantities.getProperty(symbol));
      if (quantity > 0) {
        quantity--;
        quantities.put(symbol, quantity);
        return true;
      }
    }
    return false;
  }

  /**
   * Provides the value of a stock.
   * 
   * @param symbol The stock symbol.
   * @return The stock value.
   */
  synchronized public float getStockValue(String symbol) {
    if (values.containsKey(symbol)) {
      return Float.parseFloat(values.getProperty(symbol));
    }
    return 0f;
  }

  /**
   * Provides all stock symbols present in the wallet.
   * 
   * @return All stock symbols.
   */
  synchronized public String[] getStockSymbols() {
    return values.keySet().toArray(new String[0]);
  }
}
