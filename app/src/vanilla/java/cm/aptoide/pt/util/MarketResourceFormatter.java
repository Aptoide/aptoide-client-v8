package cm.aptoide.pt.util;

import android.content.Context;

public class MarketResourceFormatter {

  private String marketName;

  public MarketResourceFormatter(String marketName) {
    this.marketName = marketName;
  }

  public String formatString(Context context, int id, String... optParamaters) {
    return context.getString(id);
  }
}
