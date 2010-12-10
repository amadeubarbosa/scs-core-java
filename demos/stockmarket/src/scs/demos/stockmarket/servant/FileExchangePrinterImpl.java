package scs.demos.stockmarket.servant;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.omg.CORBA.Object;

import scs.core.ComponentContext;
import StockMarket.ExchangePrinterPOA;

/**
 * This class implements an ExchangePrinter facet that prints stock exchange
 * information to a file.
 * 
 * @author augusto
 */
public class FileExchangePrinterImpl extends ExchangePrinterPOA {
  private ComponentContext context;
  private FileWriter writer;

  /**
   * Constructor.
   * 
   * @param context The component that this facet belongs to.
   */
  public FileExchangePrinterImpl(ComponentContext context) {
    this.context = context;
  }

  /**
   * Sets the file to be written.
   * 
   * @param f The file to be written.
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
      writer.write("Purchase of stock " + symbol + " executed.\n");
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
