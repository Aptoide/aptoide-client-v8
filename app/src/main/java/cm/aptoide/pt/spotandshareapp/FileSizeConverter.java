package cm.aptoide.pt.spotandshareapp;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Created by filipe on 07-09-2017.
 */

public class FileSizeConverter {

  public double convertToMB(double sizeInBytes) {
    double sizeInMB = sizeInBytes / (1024 * 1024);
    DecimalFormat decimalFormat = new DecimalFormat("#.##");
    DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
    decimalFormatSymbols.setDecimalSeparator('.');
    decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
    return Double.parseDouble(decimalFormat.format(sizeInMB));
  }
}
