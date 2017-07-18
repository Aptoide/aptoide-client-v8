package cm.aptoide.pt.spotandshareapp;

import android.content.Context;
import cm.aptoide.pt.preferences.AptoidePreferencesConfiguration;
import cm.aptoide.pt.spotandshare.socket.entities.Friend;
import cm.aptoide.pt.v8engine.V8Engine;

/**
 * Created by filipe on 27-06-2017.
 */

public class SpotAndShareApplication extends V8Engine {

  private cm.aptoide.pt.spotandshareandroid.SpotAndShare spotAndShare;

  @Override public void onCreate() {
    setupCrashReports(BuildConfig.CRASH_REPORTS_DISABLED);
    super.onCreate();
  }

  @Override public AptoidePreferencesConfiguration createConfiguration() {
    return new VanillaConfiguration(getDefaultSharedPreferences());
  }

  public cm.aptoide.pt.spotandshareandroid.SpotAndShare getSpotAndShare(Context context,
      Friend friend) {
    if (spotAndShare == null) {
      spotAndShare = new cm.aptoide.pt.spotandshareandroid.SpotAndShare(context, friend);
    }
    return spotAndShare;
  }
}
