/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 10/05/2016.
 */

package cm.aptoide.pt.preferences;

import android.content.Context;
import cm.aptoide.pt.annotation.Partners;

/**
 * Created by neuro on 22-04-2016.
 */
public abstract class Application extends android.app.Application {

  private static AptoidePreferencesConfiguration configuration;

  public static AptoidePreferencesConfiguration getConfiguration() {
    return Application.configuration;
  }

  //attachBaseContext is called before onCreate method
  //https://github.com/fernandodev/android-training/wiki/2.-Lifecycle,-Application,-Activities-and-Fragments
  @Override protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    // ToolboxContentProvider depends on this configuration.
    // This callback runs before Content Provider's onCreate is called.
    // Application's onCreate can't be used because it runs after ContentProvider' onCreate.
    // https://code.google.com/p/android/issues/detail?id=8727
    configuration = createConfiguration();
  }

  @Partners public abstract AptoidePreferencesConfiguration createConfiguration();
}
