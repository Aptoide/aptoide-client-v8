/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/04/2016.
 */

package cm.aptoide.pt.preferences.managed;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import cm.aptoide.pt.preferences.Application;

/**
 * Created by neuro on 21-04-2016.
 */
public class Preferences {

  private static final SharedPreferences preferences =
      PreferenceManager.getDefaultSharedPreferences(Application.getContext());

  public static SharedPreferences get() {
    return preferences;
  }
}
