package scs.demos.stockmarket.servant;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.omg.CORBA.Object;

import scs.core.servant.ComponentContext;
import StockMarket.ExchangePrinterPOA;

/**
 * @author augusto
 * 
 */
public class FileExchangePrinterImpl extends ExchangePrinterPOA {
  private ComponentContext context;
  private FileWriter writer;

  /**
   * @param context
   */
  public FileExchangePrinterImpl(ComponentContext context) {
    this.context = context;
  }

  /**
   * @param f
   */
  public void setFile(File f) {
    try {
      this.writer = new FileWriter(f);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void print(String symbol) {
    try {
      writer.write("Compra da ação " + symbol + " executada.");
      writer.flush();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Object _get_component() {
    return context.getIComponent();
  }
}
