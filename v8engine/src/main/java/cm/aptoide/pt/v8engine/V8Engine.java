/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 12/05/2016.
 */

package cm.aptoide.pt.v8engine;

import android.os.StrictMode;
import android.util.Log;

import com.squareup.leakcanary.LeakCanary;

import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.utils.SystemUtils;

/**
 * Created by neuro on 14-04-2016.
 */
public abstract class V8Engine extends DataProvider {

	private static final String TAG = V8Engine.class.getName();

	@Override
	public void onCreate() {

		SystemUtils.context = this;

		if (BuildConfig.DEBUG) {
			setupStrictMode();
			Log.w(TAG, "StrictMode setup");
		}

		super.onCreate();

		if (BuildConfig.DEBUG) {
			LeakCanary.install(this);
			Log.w(TAG, "LeakCanary installed");
		}
	}

	private void setupStrictMode() {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads()
				.detectDiskWrites()
				.detectNetwork()   // or .detectAll() for all detectable problems
				.penaltyLog()
				.build());

		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects()
				.detectLeakedClosableObjects()
				.penaltyLog()
				.penaltyDeath()
				.build());
	}
}
