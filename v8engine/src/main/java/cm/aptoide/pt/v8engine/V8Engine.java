/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.ws.responses.Subscription;
import cm.aptoide.pt.database.accessors.Database;
import cm.aptoide.pt.database.accessors.DeprecatedDatabase;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepository;
import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.StoreUtils;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.downloadmanager.DownloadService;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.SecurityUtils;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.deprecated.SQLiteDatabaseHelper;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.flurry.android.FlurryAgent;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import java.util.Collections;
import java.util.List;
import lombok.Cleanup;
import lombok.Getter;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by neuro on 14-04-2016.
 */
public abstract class V8Engine extends DataProvider {

  private static final String TAG = V8Engine.class.getName();

  @Getter static DownloadService downloadService;
  private RefWatcher refWatcher;

  public static void loadStores() {

    AptoideAccountManager.getUserRepos().subscribe(subscriptions -> {
      @Cleanup Realm realm = DeprecatedDatabase.get();
      for (Subscription subscription : subscriptions) {
        Store store = new Store();

        store.setDownloads(Long.parseLong(subscription.getDownloads()));
        store.setIconPath(subscription.getAvatarHd() != null ? subscription.getAvatarHd()
            : subscription.getAvatar());
        store.setStoreId(subscription.getId().longValue());
        store.setStoreName(subscription.getName());
        store.setTheme(subscription.getTheme());

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(store);
        realm.commitTransaction();
      }

      DataproviderUtils.checkUpdates();
    });
  }

  public static void loadUserData() {
    loadStores();
  }

  public static void clearUserData() {
    clearStores();
  }

  private static void clearStores() {
    @Cleanup Realm realm = DeprecatedDatabase.get();
    realm.beginTransaction();
    realm.delete(Store.class);
    realm.commitTransaction();

    StoreUtils.subscribeStore(getConfiguration().getDefaultStore(), null, null);
  }

  public static RefWatcher getRefWatcher(Context context) {
    V8Engine app = (V8Engine) context.getApplicationContext();
    return app.refWatcher;
  }

  @Override public void onCreate() {
    long l = System.currentTimeMillis();
    AptoideUtils.setContext(this);

    //
    // super
    //
    super.onCreate();

    DeprecatedDatabase.initialize(this);
    Database.initialize(this);

    generateAptoideUUID().subscribe();

    SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(this);
    Analytics.LocalyticsSessionControl.firstSession(sPref);
    Analytics.Lifecycle.Application.onCreate(this);

    new FlurryAgent.Builder().withLogEnabled(false).build(this, BuildConfig.FLURRY_KEY);

    if (BuildConfig.DEBUG) {
      refWatcher = LeakCanary.install(this);
      //registerActivityLifecycleCallbacks(new LeakCAnaryActivityWatcher(refWatcher));
      Log.w(TAG, "LeakCanary installed");
    } else {
      refWatcher = RefWatcher.DISABLED;
    }

    if (SecurePreferences.isFirstRun()) {
      loadInstalledApps().doOnNext(o -> {
        if (AptoideAccountManager.isLoggedIn()) {
          if (!SecurePreferences.isUserDataLoaded()) {
            loadUserData();
            SecurePreferences.setUserDataLoaded();
          }
        } else {
          generateAptoideUUID().subscribe(success -> addDefaultStore());
        }
        //			    SecurePreferences.setFirstRun(false);    //jdandrade - Disabled this line so i could run first run wizard.
      }).subscribe();
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

    setupCrashlytics();

    AptoideDownloadManager.getInstance()
        .init(this, new DownloadNotificationActionsActionsInterface(),
            new DownloadManagerSettingsI());

    // setupCurrentActivityListener();

    if (BuildConfig.DEBUG) {
      setupStrictMode();
      Log.w(TAG, "StrictMode setup");
    }

    // this will trigger the migration if needed
    // FIXME: 24/08/16 sithengineer the following line should be removed when no more SQLite -> Realm migration is needed
    SQLiteDatabase db = new SQLiteDatabaseHelper(this).getWritableDatabase();
    db.close();

    Logger.d(TAG, "onCreate took " + (System.currentTimeMillis() - l) + " millis.");
  }

  Observable<String> generateAptoideUUID() {
    return Observable.fromCallable(
        () -> new IdsRepository(SecurePreferencesImplementation.getInstance(),
            this).getAptoideClientUUID()).subscribeOn(Schedulers.computation());
  }

  private void setupCrashlytics() {
    Crashlytics crashlyticsKit = new Crashlytics.Builder().core(
        new CrashlyticsCore.Builder().disabled(!BuildConfig.FABRIC_CONFIGURED).build()).build();
    Fabric.with(this, crashlyticsKit);
  }

  //
  // Strict Mode
  //

  private void addDefaultStore() {
    StoreUtils.subscribeStore(getConfiguration().getDefaultStore(),
        getStoreMeta -> DataproviderUtils.checkUpdates(), null);
  }

  //
  // Leak Canary
  //

  private Observable<?> loadInstalledApps() {
    return Observable.fromCallable(() -> {
      @Cleanup Realm realm = DeprecatedDatabase.get();
      DeprecatedDatabase.dropTable(Installed.class, realm);
      // FIXME: 15/07/16 sithengineer to fred -> try this instead to avoid re-creating the table: realm.delete(Installed.class);

      List<PackageInfo> installedApps = AptoideUtils.SystemU.getAllInstalledApps();
      Log.d(TAG, "Found " + installedApps.size() + " user installed apps.");

      // Installed apps are inserted in database based on their firstInstallTime. Older comes first.
      Collections.sort(installedApps,
          (lhs, rhs) -> (int) ((lhs.firstInstallTime - rhs.firstInstallTime) / 1000));

      for (PackageInfo packageInfo : installedApps) {
        Installed installed = new Installed(packageInfo, getPackageManager());
        DeprecatedDatabase.save(installed, realm);
      }
      return null;
    }).subscribeOn(Schedulers.io());
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

  //	private static class LeakCAnaryActivityWatcher implements ActivityLifecycleCallbacks {
  //
  //		private final RefWatcher refWatcher;
  //
  //		private LeakCAnaryActivityWatcher(RefWatcher refWatcher) {
  //			this.refWatcher = refWatcher;
  //		}
  //
  //		@Override
  //		public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
  //
  //		}
  //
  //		@Override
  //		public void onActivityStarted(Activity activity) {
  //
  //		}
  //
  //		@Override
  //		public void onActivityResumed(Activity activity) {
  //
  //		}
  //
  //		@Override
  //		public void onActivityPaused(Activity activity) {
  //
  //		}
  //
  //		@Override
  //		public void onActivityStopped(Activity activity) {
  //
  //		}
  //
  //		@Override
  //		public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
  //
  //		}
  //
  //		@Override
  //		public void onActivityDestroyed(Activity activity) {
  //			refWatcher.watch(activity);
  //		}
  //	}
}
