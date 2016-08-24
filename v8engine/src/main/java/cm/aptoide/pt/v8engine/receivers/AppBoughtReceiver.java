/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 05/08/2016.
 */

package cm.aptoide.pt.v8engine.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

/**
 * Created by sithengineer on 05/08/16.
 */
public abstract class AppBoughtReceiver extends BroadcastReceiver {

	public static final String APP_ID = "appId";
	public static final String APP_BOUGHT = "APP_BOUGHT";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (TextUtils.equals(action, APP_BOUGHT)) {
			appBought(intent.getLongExtra(APP_ID, -1));
		}
	}

	public abstract void appBought(long appId);
}
