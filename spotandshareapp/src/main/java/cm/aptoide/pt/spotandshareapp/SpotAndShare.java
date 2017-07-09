package cm.aptoide.pt.spotandshareapp;

import android.content.Context;

/**
 * Created by filipe on 05-07-2017.
 */

public class SpotAndShare {

  private static cm.aptoide.pt.spotandshareandroid.SpotAndShare instance;

  public static cm.aptoide.pt.spotandshareandroid.SpotAndShare getInstance(Context context) {
    if (instance == null) {
      instance = new cm.aptoide.pt.spotandshareandroid.SpotAndShare(context);
    }
    return instance;
  }
}
