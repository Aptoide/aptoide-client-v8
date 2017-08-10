package cm.aptoide.pt.spotandshareapp;

import android.content.Context;
import android.support.multidex.MultiDex;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.V8Engine;
import cm.aptoide.pt.preferences.AptoidePreferencesConfiguration;
import cm.aptoide.pt.spotandshare.socket.entities.Friend;

/**
 * Created by filipe on 27-06-2017.
 */

public class SpotAndShareApplication extends V8Engine {

  private cm.aptoide.pt.spotandshareandroid.SpotAndShare spotAndShare;
  public SpotAndShareUserManager spotAndShareUserManager;

  @Override protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    MultiDex.install(base);
  }

  @Override public void onCreate() {
    MultiDex.install(this);
    setupCrashReports(BuildConfig.CRASH_REPORTS_DISABLED);
    super.onCreate();
  }

  @Override public AptoidePreferencesConfiguration createConfiguration() {
    return new VanillaConfiguration(getDefaultSharedPreferences());
  }

  public SpotAndShareUserManager getSpotAndShareUserManager() {
    if (spotAndShareUserManager == null) {
      spotAndShareUserManager = new SpotAndShareUserManager(this, new SpotAndShareUserPersister(
          getSharedPreferences(SpotAndShareUserPersister.SHARED_PREFERENCES_NAME,
              Context.MODE_PRIVATE)));
    }
    return spotAndShareUserManager;
  }

  public cm.aptoide.pt.spotandshareandroid.SpotAndShare getSpotAndShare() {
    if (spotAndShare == null) {
      Friend friend = new Friend(getSpotAndShareUserManager().getUser()
          .getUsername());
      spotAndShare = new cm.aptoide.pt.spotandshareandroid.SpotAndShare(this, friend);
    }
    return spotAndShare;
  }

  public void updateFriendProfileOnSpotAndShare() {
    spotAndShare = null;
    getSpotAndShare();
  }
}
