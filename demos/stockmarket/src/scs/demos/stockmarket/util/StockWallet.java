package scs.demos.stockmarket.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author augusto
 * 
 */
public class StockWallet {
  private Properties values;
  private Properties quantities;

  /**
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
   * @param symbol
   * @return True se a venda foi concretizada. False caso contrário.
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
   * @param symbol The stock symbol.
   * @return The stock value.
   */
  synchronized public float getStockValue(String symbol) {
    if (values.containsKey(symbol)) {
      // Simbolo encontrado; retorna seu valor
      return Float.parseFloat(values.getProperty(symbol));
    }
    return 0f;
  }

  /**
   * @return All stock symbols.
   */
  synchronized public String[] getStockSymbols() {
    return values.keySet().toArray(new String[0]);
  }
}
