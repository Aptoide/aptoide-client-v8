package cm.aptoide.pt.editorial;

import java.text.DecimalFormat;

public class ViewsFormatter {

  private static final int NUMBER_OF_SUFFIXES = 2;

  public static String formatNumberOfViews(String views) {
    int length = views.length() / 4;
    if (length > NUMBER_OF_SUFFIXES) length = NUMBER_OF_SUFFIXES;
    double nViews = Double.parseDouble(views);
    double dividedViews = get3DigitNumber(nViews, length);
    String suffix = getSuffix(length);
    DecimalFormat numberFormat = getDecimalFormat(dividedViews);
    return numberFormat.format(dividedViews) + suffix;
  }

  private static DecimalFormat getDecimalFormat(double views) {
    DecimalFormat numberFormat;
    if (views < 10) {
      numberFormat = new DecimalFormat("#.##");
    } else if (views < 100) {
      numberFormat = new DecimalFormat("#.#");
    } else {
      numberFormat = new DecimalFormat("#");
    }
    return numberFormat;
  }

  private static double get3DigitNumber(double views, int length) {
    return views / Math.pow(1000, length);
  }

  private static String getSuffix(int length) {
    String[] suffixes = { "", "k", "M" };
    String suffix = "";
    if (length < suffixes.length) {
      suffix = suffixes[length];
    }
    return suffix;
  }
}
