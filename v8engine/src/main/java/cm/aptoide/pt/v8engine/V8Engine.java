/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 05/07/2016.
 */

package cm.aptoide.pt.v8engine;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.os.IBinder;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.facebook.stetho.Stetho;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.squareup.leakcanary.LeakCanary;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.ws.responses.Subscription;
import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.StoreUtils;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.downloadmanager.DownloadService;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.SecurityUtils;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import lombok.Cleanup;
import lombok.Getter;
import rx.functions.Action1;

/**
 * Created by neuro on 14-04-2016.
 */
public abstract class V8Engine extends DataProvider {

	private static final String TAG = V8Engine.class.getName();

	@Getter static DownloadService downloadService;

	private ServiceConnection downloadServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			DownloadService.LocalBinder binder = (DownloadService.LocalBinder) service;
			downloadService = binder.getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
		}
	};

	public static void loadStores() {

		AptoideAccountManager.getUserRepos().subscribe(new Action1<List<Subscription>>() {
			@Override
			public void call(List<Subscription> subscriptions) {
				@Cleanup
				Realm realm = Database.get(getContext());
				for (Subscription subscription : subscriptions) {
					Store store = new Store();

					store.setDownloads(Long.parseLong(subscription.getDownloads()));
					store.setIconPath(subscription.getAvatarHd() != null ? subscription.getAvatarHd() : subscription.getAvatar());
					store.setStoreId(subscription.getId().longValue());
					store.setStoreName(subscription.getName());
					store.setTheme(subscription.getTheme());

					realm.beginTransaction();
					realm.copyToRealmOrUpdate(store);
					realm.commitTransaction();
				}
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
			setAdvertisingId();
			setAndroidId();
			loadInstalledApps();
			DataproviderUtils.checkUpdates();

			if (AptoideAccountManager.isLoggedIn()) {
				if (!SecurePreferences.isUserDataLoaded()) {
					loadUserData();
					SecurePreferences.setUserDataLoaded();
				}
			} else {
				addDefaultStore();
			}
			SecurePreferences.setFirstRun(false);
		}

		final int appSignature = SecurityUtils.checkAppSignature(this);
		if (appSignature != SecurityUtils.VALID_APP_SIGNATURE) {
			Logger.e(TAG, "app signature is not valid!");
		}

		if (SecurityUtils.checkEmulator()) {
			Logger.w(TAG, "application is running on an emulator");
		}

		if (SecurityUtils.checkDebuggable(this)) {
			Logger.w(TAG, "application has debug flag active");
		}

		if (BuildConfig.DEBUG) {
			Stetho.initializeWithDefaults(this);
		}

		setupCrashlytics();

		Logger.d(TAG, "onCreate took " + (System.currentTimeMillis() - l) + " millis.");

		AptoideDownloadManager.getInstance()
				.init(context, downloadServiceConnection, new DownloadNotificationActionsActionsInterface(), new
						DownloadManagerSettingsI());
	}

	private void setupCrashlytics() {
		Crashlytics crashlyticsKit = new Crashlytics.Builder().core(new CrashlyticsCore.Builder().disabled(!BuildConfig.FABRIC_CONFIGURED).build()).build();
		Fabric.with(this, crashlyticsKit);
	}

	private void setAndroidId() {
		SecurePreferences.setAndroidId(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
	}

	private void setAdvertisingId() {
		AptoideUtils.ThreadU.runOnIoThread(() -> {
			if (DataproviderUtils.AdNetworksUtils.isGooglePlayServicesAvailable()) {
				try {
					SecurePreferences.setGoogleAdvertisingId(AdvertisingIdClient.getAdvertisingIdInfo(V8Engine.this).getId());
				} catch (Exception e) {
					e.printStackTrace();
					SecurePreferences.setAdvertisingId(generateRandomAdvertisingID());
				}
			} else {
				SecurePreferences.setAdvertisingId(generateRandomAdvertisingID());
			}
		});
	}

	private String generateRandomAdvertisingID() {
		byte[] data = new byte[16];
		String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
		if (deviceId == null) {
			deviceId = UUID.randomUUID().toString();
		}

		SecureRandom secureRandom = new SecureRandom();
		secureRandom.setSeed(deviceId.hashCode());
		secureRandom.nextBytes(data);
		return UUID.nameUUIDFromBytes(data).toString();
	}

	private void addDefaultStore() {
		StoreUtils.subscribeStore(getConfiguration().getDefaultStore(), null, null);
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
