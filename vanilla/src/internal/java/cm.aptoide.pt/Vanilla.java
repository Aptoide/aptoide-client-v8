/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 01/07/2016.
 */

package cm.aptoide.pt;

import android.preference.PreferenceManager;
import cm.aptoide.pt.preferences.secure.DevSecureKeys;
import cm.aptoide.pt.utils.AptoideUtils;

/**
 * Created by neuro on 10-05-2016.
 */
public class Vanilla extends AptoideBase {

  @Override public void onCreate() {
    clearAppDataOnNewBuild();
    super.onCreate();
    activateLogger();
    // FIXME: comment the next line to avoid a sensible app when debugging
    // TODO: move the decision of using this to a gradle flag
    //setupStrictMode();
  }

  private void clearAppDataOnNewBuild() {
    if (getLastRunVersionCode() != BuildConfig.VERSION_CODE) {
      AptoideUtils.SystemU.clearApplicationData(this);
      PreferenceManager.getDefaultSharedPreferences(this)
          .edit()
          .clear()
          .commit();
      setLastRunVersionCode(BuildConfig.VERSION_CODE);
    }
  }

  private int getLastRunVersionCode() {
    return PreferenceManager.getDefaultSharedPreferences(this)
        .getInt(DevSecureKeys.LAST_RUN_VERSION_CODE, -1);
  }

  private void setLastRunVersionCode(int versionCode) {
    PreferenceManager.getDefaultSharedPreferences(this)
        .edit()
        .putInt(DevSecureKeys.LAST_RUN_VERSION_CODE, versionCode)
        .apply();
  }
}
