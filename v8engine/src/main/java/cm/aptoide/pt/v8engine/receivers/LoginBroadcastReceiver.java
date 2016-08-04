/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 03/08/2016.
 */

package cm.aptoide.pt.v8engine.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.services.ValidatePaymentsService;

/**
 * Created by neuro on 24-05-2016.
 */
public class LoginBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (AptoideAccountManager.LOGIN.equals(intent.getAction())) {
			V8Engine.loadUserData();
			context.startService(ValidatePaymentsService.getIntent(context));
		} else if (AptoideAccountManager.LOGOUT.equals(intent.getAction())) {
			V8Engine.clearUserData();
		}
	}
}
