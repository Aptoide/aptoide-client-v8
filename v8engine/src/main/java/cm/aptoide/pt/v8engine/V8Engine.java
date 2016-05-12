/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 12/05/2016.
 */

package cm.aptoide.pt.v8engine;

import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;

import com.squareup.leakcanary.LeakCanary;

import cm.aptoide.pt.database.AptoideRealmMigration;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.utils.SystemUtils;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by neuro on 14-04-2016.
 */
public abstract class V8Engine extends DataProvider {

	public static String KEY = "w6ns1CPHfS9FdY20QCXw";

	private static final String TAG = V8Engine.class.getName();

	@Override
	public void onCreate() {

		if (BuildConfig.DEBUG) {
			setupStrictMode();
			Log.w(TAG, "StrictMode setup");
		}

		super.onCreate();

		if (BuildConfig.DEBUG) {
			LeakCanary.install(this);
			Log.w(TAG, "LeakCanary installed");
		}

		// app setups go here
		setupRealm();
		setupAptoideInternals();
	}

	private void setupRealm() {

		StringBuilder strBuilder = new StringBuilder(KEY);
		strBuilder.append(MainActivity.KEY);
		strBuilder.append(StorePagerAdapter.KEY);
		strBuilder.append(extract(cm.aptoide.pt.model.BuildConfig.APPLICATION_ID));
		strBuilder.append(extract(cm.aptoide.pt.networkclient.BuildConfig.APPLICATION_ID));
		strBuilder.append(extract(cm.aptoide.pt.utils.BuildConfig.APPLICATION_ID));
		strBuilder.append(extract(cm.aptoide.pt.database.BuildConfig.APPLICATION_ID));
		strBuilder.append(extract(cm.aptoide.pt.preferences.BuildConfig.APPLICATION_ID));

		RealmConfiguration realmConfiguration =
				new RealmConfiguration.Builder(this)
						.name(getConfiguration().getAppId())
						.encryptionKey(strBuilder.toString().getBytes())
						// Must be bumped when the schema changes
						.schemaVersion(cm.aptoide.pt.database.BuildConfig.VERSION_CODE)
						// Migration to run instead of throwing an exception
						.migration(new AptoideRealmMigration())
						.build();
		Realm.setDefaultConfiguration(realmConfiguration);
	}

	private String extract(String str) {
		return TextUtils.substring(str, str.lastIndexOf('.'), str.length());
	}

	private void setupAptoideInternals(){
		SystemUtils.context = this;
		KEY = "";
		MainActivity.KEY = "";
		StorePagerAdapter.KEY = "";
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
