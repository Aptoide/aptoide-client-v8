/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine;

import android.accounts.AccountManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.annotation.Partners;
import cm.aptoide.pt.crashreports.ConsoleLogger;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.crashreports.CrashlyticsCrashLogger;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.Database;
import cm.aptoide.pt.database.accessors.DownloadAccessor;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreMetaRequest;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.PRNGFixes;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.shareappsandroid.ShareApps;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.FileUtils;
import cm.aptoide.pt.utils.SecurityUtils;
import cm.aptoide.pt.v8engine.account.AccountDynamicRequestFactory;
import cm.aptoide.pt.v8engine.account.DatabaseStoreDataPersist;
import cm.aptoide.pt.v8engine.account.ExternalServicesLoginAvailability;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.AccountAnalytcs;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events.SpotAndShareAnalytics;
import cm.aptoide.pt.v8engine.analytics.abtesting.ABTestManager;
import cm.aptoide.pt.v8engine.configuration.ActivityProvider;
import cm.aptoide.pt.v8engine.configuration.FragmentProvider;
import cm.aptoide.pt.v8engine.configuration.implementation.ActivityProviderImpl;
import cm.aptoide.pt.v8engine.configuration.implementation.FragmentProviderImpl;
import cm.aptoide.pt.v8engine.deprecated.SQLiteDatabaseHelper;
import cm.aptoide.pt.v8engine.download.TokenHttpClient;
import cm.aptoide.pt.v8engine.filemanager.CacheHelper;
import cm.aptoide.pt.v8engine.filemanager.FileManager;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.repository.UpdateRepository;
import cm.aptoide.pt.v8engine.util.StoreCredentialsProviderImpl;
import cm.aptoide.pt.v8engine.util.StoreUtils;
import cm.aptoide.pt.v8engine.util.StoreUtilsProxy;
import cm.aptoide.pt.v8engine.view.MainActivity;
import cm.aptoide.pt.v8engine.view.recycler.DisplayableWidgetMapping;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.common.GoogleApiAvailability;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

/**
 * Created by neuro on 14-04-2016.
 */
public abstract class V8Engine extends DataProvider {

  private static final String TAG = V8Engine.class.getName();

  //@Getter static DownloadService downloadService;
  @Getter private static FragmentProvider fragmentProvider;
  @Getter private static ActivityProvider activityProvider;
  @Getter private static DisplayableWidgetMapping displayableWidgetMapping;
  @Setter @Getter private static boolean autoUpdateWasCalled = false;
  @Getter @Setter private static ShareApps shareApps;

  private static AptoideClientUUID aptoideClientUUID;
  private AptoideAccountManager accountManager;

  public static void clearUserData(AptoideAccountManager accountManager) {
    AccessorFactory.getAccessorFor(Store.class).removeAll();
    StoreUtils.subscribeStore(getConfiguration().getDefaultStore(), null, null, accountManager,
        new BaseBodyInterceptor(aptoideClientUUID, accountManager),
        new StoreCredentialsProviderImpl());
    regenerateUserAgent(accountManager);
  }

  private static void regenerateUserAgent(final AptoideAccountManager accountManager) {
    Observable.just(null)
        .observeOn(Schedulers.computation())
        .doOnNext(o -> SecurePreferences.setUserAgent(
            AptoideUtils.NetworkUtils.getDefaultUserAgent(aptoideClientUUID,
                () -> accountManager.getUserEmail(), AptoideUtils.Core.getDefaultVername(),
                getConfiguration().getPartnerId())))
        .subscribe();
  }

  /**
   * call after this instance onCreate()
   */
  protected void activateLogger() {
    Logger.setDBG(true);
  }

  @Partners @Override public void onCreate() {
    //
    // apply security fixes
    //
    try {
      PRNGFixes.apply();
    } catch (Exception e) {
      CrashReport.getInstance().log(e);
    }

    //
    // super
    //
    super.onCreate();

    //
    // onCreate
    //

    long l = System.currentTimeMillis();
    fragmentProvider = createFragmentProvider();
    activityProvider = createActivityProvider();
    displayableWidgetMapping = createDisplayableWidgetMapping();
    aptoideClientUUID = new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(), this);
    shareApps = new ShareApps(new SpotAndShareAnalytics());

    //
    // do not erase this code. it is useful to figure out when someone forgot to attach an error handler when subscribing and the app
    // is crashing in Rx without a proper stack trace
    //
    //if (BuildConfig.DEBUG) {
    //  RxJavaPlugins.getInstance().registerObservableExecutionHook(new RxJavaStackTracer());
    //}

    Database.initialize(this);

    generateAptoideUUID().subscribe();

    // this will trigger the migration if needed
    SQLiteDatabase db = new SQLiteDatabaseHelper(this).getWritableDatabase();
    db.close();

    final AptoideAccountManager accountManager = getAccountManager();
    if (SecurePreferences.shouldLogoutUser()) {
      accountManager.removeAccount();
      SecurePreferences.setLogoutUser(false);
    }

    regenerateUserAgent(accountManager);

    SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(this);
    Analytics.LocalyticsSessionControl.firstSession(sPref);
    Analytics.Lifecycle.Application.onCreate(this);
    Logger.setDBG(ManagerPreferences.isDebug() || BuildConfig.DEBUG);
    new FlurryAgent.Builder().withLogEnabled(false).build(this, BuildConfig.FLURRY_KEY);

    if (SecurePreferences.isFirstRun()) {
      createShortCut();
      PreferenceManager.setDefaultValues(this, R.xml.settings, false);
      if (accountManager.isLoggedIn() && ManagerPreferences.isFirstRunV7()) {
        accountManager.removeAccount();
      }
      loadInstalledApps().doOnNext(o -> {
        if (accountManager.isLoggedIn()) {

          if (!SecurePreferences.isUserDataLoaded()) {
            loadUserData(accountManager);
            SecurePreferences.setUserDataLoaded();
          }
        } else {
          final BaseBodyInterceptor bodyInterceptor =
              new BaseBodyInterceptor(aptoideClientUUID, accountManager);

          final StoreCredentialsProviderImpl storeCredentials = new StoreCredentialsProviderImpl();

          StoreUtilsProxy proxy =
              new StoreUtilsProxy(accountManager, bodyInterceptor, storeCredentials,
                  AccessorFactory.getAccessorFor(Store.class));

          BaseRequestWithStore.StoreCredentials defaultStoreCredentials =
              storeCredentials.get(getConfiguration().getDefaultStore());

          generateAptoideUUID().flatMap(__ -> proxy.addDefaultStore(
              GetStoreMetaRequest.of(defaultStoreCredentials, bodyInterceptor), accountManager,
              defaultStoreCredentials).doOnCompleted(() -> checkUpdates()).toObservable())
              .subscribe(success -> {
              }, err -> CrashReport.getInstance().log(err));
        }
        SecurePreferences.setFirstRun(false);
      }).subscribe();

      // load picture, name and email
      accountManager.syncCurrentAccount().subscribe(() -> {
      }, e -> {
        CrashReport.getInstance().log(e);
      });
    } else {
      loadInstalledApps().subscribe();
    }

    final int appSignature = SecurityUtils.checkAppSignature(this);
    if (appSignature != SecurityUtils.VALID_APP_SIGNATURE) {
      Logger.w(TAG, "app signature is not valid!");
    }

    if (SecurityUtils.checkEmulator()) {
      Logger.w(TAG, "application is running on an emulator");
    }

    if (SecurityUtils.checkDebuggable(this)) {
      Logger.w(TAG, "application has debug flag active");
    }

    final DownloadAccessor downloadAccessor = AccessorFactory.getAccessorFor(Download.class);
    FileManager fileManager = FileManager.build();
    AptoideDownloadManager.getInstance()
        .init(this, new DownloadNotificationActionsActionsInterface(),
            new DownloadManagerSettingsI(), downloadAccessor, CacheHelper.build(),
            new FileUtils(action -> Analytics.File.moveFile(action)),
            new TokenHttpClient(aptoideClientUUID, () -> accountManager.getUserEmail(),
                getConfiguration().getPartnerId(), accountManager).customMake(),
            new DownloadAnalytics(Analytics.getInstance()));

    fileManager.purgeCache()
        .subscribe(cleanedSize -> Logger.d(TAG,
            "cleaned size: " + AptoideUtils.StringU.formatBytes(cleanedSize, false)), throwable -> {
          CrashReport.getInstance().log(throwable);
        });

    ABTestManager.getInstance()
        .initialize(aptoideClientUUID.getUniqueIdentifier())
        .subscribe(success -> {
        }, throwable -> {
          CrashReport.getInstance().log(throwable);
        });

    Logger.d(TAG, "onCreate took " + (System.currentTimeMillis() - l) + " millis.");
  }

  @Override protected TokenInvalidator getTokenInvalidator() {
    return new TokenInvalidator() {
      @Override public Single<String> invalidateAccessToken() {
        return accountManager.refreshAccountToken()
            .andThen(accountManager.getAccountAsync())
            .map(account -> account.getToken());
      }
    };
  }

  @Partners protected FragmentProvider createFragmentProvider() {
    return new FragmentProviderImpl();
  }

  @Partners protected ActivityProvider createActivityProvider() {
    return new ActivityProviderImpl();
  }

  @Partners protected DisplayableWidgetMapping createDisplayableWidgetMapping() {
    return DisplayableWidgetMapping.getInstance();
  }

  Observable<String> generateAptoideUUID() {
    return Observable.fromCallable(() -> aptoideClientUUID.getUniqueIdentifier())
        .subscribeOn(Schedulers.computation());
  }

  public AptoideAccountManager getAccountManager() {
    if (accountManager == null) {

      final IdsRepositoryImpl aptoideClientUUID =
          new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(), this);

      final DatabaseStoreDataPersist dataPersist =
          new DatabaseStoreDataPersist(AccessorFactory.getAccessorFor(Store.class),
              new DatabaseStoreDataPersist.DatabaseStoreMapper());

      final ExternalServicesLoginAvailability loginAvailability =
          new ExternalServicesLoginAvailability(this, getConfiguration(),
              GoogleApiAvailability.getInstance());

      final AccountDynamicRequestFactory requestFactory =
          new AccountDynamicRequestFactory(aptoideClientUUID);

      accountManager = new AptoideAccountManager(this, getConfiguration(), AccountManager.get(this),
          aptoideClientUUID, loginAvailability, new AccountAnalytcs(),
          getConfiguration().getAccountType(), requestFactory, dataPersist);
    }
    return accountManager;
  }

  @Partners public void createShortCut() {
    Intent shortcutIntent = new Intent(this, MainActivity.class);
    shortcutIntent.setAction(Intent.ACTION_MAIN);
    Intent intent = new Intent();
    intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
    intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Aptoide");
    intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
        Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_launcher));
    intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
    getApplicationContext().sendBroadcast(intent);
  }

  private Observable<?> loadInstalledApps() {
    return Observable.fromCallable(() -> {
      // remove the current installed apps
      AccessorFactory.getAccessorFor(Installed.class).removeAll();

      // get the installed apps
      List<PackageInfo> installedApps = AptoideUtils.SystemU.getAllInstalledApps();
      Logger.v(TAG, "Found " + installedApps.size() + " user installed apps.");

      // Installed apps are inserted in database based on their firstInstallTime. Older comes first.
      Collections.sort(installedApps,
          (lhs, rhs) -> (int) ((lhs.firstInstallTime - rhs.firstInstallTime) / 1000));

      // return sorted installed apps
      return installedApps;
    })  // transform installation package into Installed table entry and save all the data
        .flatMapIterable(list -> list)
        .map(packageInfo -> new Installed(packageInfo))
        .toList()
        .doOnNext(list -> {
          AccessorFactory.getAccessorFor(Installed.class).insertAll(list);
        })
        .subscribeOn(Schedulers.io());
  }

  public static void loadUserData(AptoideAccountManager accountManager) {
    regenerateUserAgent(accountManager);
  }

  private static void checkUpdates() {
    UpdateRepository repository = RepositoryFactory.getUpdateRepository(DataProvider.getContext());
    repository.sync(true)
        .andThen(repository.getAll(false))
        .first()
        .subscribe(updates -> Logger.d(TAG, "updates are up to date now"), throwable -> {
          CrashReport.getInstance().log(throwable);
        });
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

  @Partners protected void setupCrashReports(boolean isDisabled) {
    CrashReport.getInstance()
        .addLogger(new CrashlyticsCrashLogger(this, isDisabled))
        .addLogger(new ConsoleLogger());
  }

  //
  // Strict Mode
  //

  /**
   * do not erase this method. it should be called in internal and dev Application class
   * of Vanilla module
   */
  protected void setupStrictMode() {
    StrictMode.setThreadPolicy(
        new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());

    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedClosableObjects()
        .detectLeakedClosableObjects()
        .penaltyLog()
        .penaltyDeath()
        .build());
  }
}
