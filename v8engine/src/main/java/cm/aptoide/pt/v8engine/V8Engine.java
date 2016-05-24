/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 24/05/2016.
 */

package cm.aptoide.pt.v8engine;

import android.os.StrictMode;
import android.util.Log;

import com.squareup.leakcanary.LeakCanary;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.ws.responses.GetUserRepoSubscription;
import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.StoreUtils;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.utils.SystemUtils;
import io.realm.Realm;
import lombok.Cleanup;

/**
 * Created by neuro on 14-04-2016.
 */
public abstract class V8Engine extends DataProvider {

	private static final String TAG = V8Engine.class.getName();

	public static void loadStores() {

		AptoideAccountManager.getUserRepos().subscribe(subscriptions -> {
			@Cleanup Realm realm = Database.get(getContext());
			for (GetUserRepoSubscription.Subscription subscription : subscriptions) {
				Store store = new Store();

				store.setDownloads(Long.parseLong(subscription.getDownloads()));
				store.setIconPath(subscription.getAvatarHd() != null ? subscription.getAvatarHd() : subscription
						.getAvatar());
				store.setStoreId(subscription.getId().longValue());
				store.setStoreName(subscription.getName());
				store.setTheme(subscription.getTheme());

				realm.beginTransaction();
				realm.copyToRealmOrUpdate(store);
				realm.commitTransaction();
			}
		});
	}

	public static void loadUserData() {
		loadStores();
	}

	public static void clearUserData() {
		clearStores();
	}

	private static void clearStores() {
		@Cleanup Realm realm = Database.get(V8Engine.getContext());
		realm.beginTransaction();
		realm.delete(Store.class);
		realm.commitTransaction();

		StoreUtils.subscribeStore(getConfiguration().getDefaultStore(), null, null);
	}

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

		if (SecurePreferences.isFirstRun()) {
			if (AptoideAccountManager.isLoggedIn()) {
				if (!SecurePreferences.isUserDataLoaded()) {
					loadUserData();
					SecurePreferences.setUserDataLoaded();
				}
			}
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
