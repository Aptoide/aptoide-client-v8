/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/05/2016.
 */

package cm.aptoide.pt.v8engine;

import android.content.pm.PackageInfo;
import android.os.StrictMode;
import android.util.Log;

import com.squareup.leakcanary.LeakCanary;

import java.util.Collections;
import java.util.List;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.ws.responses.GetUserRepoSubscription;
import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.utils.SystemUtils;
import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.StoreUtils;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.utils.AptoideUtils;
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
		long l = System.currentTimeMillis();
		AptoideUtils.setContext(this);

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
			loadInstalledApps();
			DataproviderUtils.checkUpdates();

			if (AptoideAccountManager.isLoggedIn()) {
				if (!SecurePreferences.isUserDataLoaded()) {
					loadUserData();
					SecurePreferences.setUserDataLoaded();
				}
			}
		}
		Log.d(TAG, "onCreate took " + (System.currentTimeMillis() - l) + " millis.");
		AptoideDownloadManager.getInstance().init(this);
	}

	private void loadInstalledApps() {
		@Cleanup Realm realm = Database.get(this);
		Database.dropTable(Installed.class, realm);

		List<PackageInfo> installedApps = AptoideUtils.SystemU.getUserInstalledApps();
		Log.d(TAG, "Found " + installedApps.size() + " user installed apps.");

		// Installed apps are inserted in database based on their firstInstallTime. Older comes first.
		Collections.sort(installedApps, (lhs, rhs) -> (int) ((lhs.firstInstallTime - rhs.firstInstallTime) / 1000));

		for (PackageInfo packageInfo : installedApps) {
			Installed installed = new Installed(packageInfo, getPackageManager());
			Database.save(installed, realm);
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
