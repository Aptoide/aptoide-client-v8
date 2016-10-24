/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 08/08/2016.
 */

package cm.aptoide.pt.v8engine.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.actions.UserData;
import cm.aptoide.pt.dataprovider.repository.IdsRepository;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.V8Engine;

/**
 * Created by neuro on 24-05-2016.
 */
public class LoginBroadcastReceiver extends BroadcastReceiver {

  @Override public void onReceive(Context context, Intent intent) {
    if (AptoideAccountManager.LOGIN.equals(intent.getAction())) {
      V8Engine.loadUserData();
    } else if (AptoideAccountManager.LOGOUT.equals(intent.getAction())) {
      V8Engine.clearUserData();
    }
  }
}
