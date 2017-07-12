package cm.aptoide.pt.spotandshareapp;

import android.content.Context;
import cm.aptoide.pt.spotandshare.socket.entities.Friend;

/**
 * Created by filipe on 05-07-2017.
 */

public class SpotAndShare {

  private static cm.aptoide.pt.spotandshareandroid.SpotAndShare instance;

  private static final Friend FRIEND = new Friend("dummy_username");

  public static cm.aptoide.pt.spotandshareandroid.SpotAndShare getInstance(Context context) {
    if (instance == null) {
      instance = new cm.aptoide.pt.spotandshareandroid.SpotAndShare(context, FRIEND);
    }
    return instance;
  }
}
