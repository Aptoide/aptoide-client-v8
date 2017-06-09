/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.ws.responses.Subscription;
import cm.aptoide.pt.actions.UserData;
import cm.aptoide.pt.crashreports.AptoideCrashLogger;
import cm.aptoide.pt.crashreports.CrashReports;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.Database;
import cm.aptoide.pt.database.accessors.DownloadAccessor;
import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.PRNGFixes;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.FileUtils;
import cm.aptoide.pt.utils.SecurityUtils;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.AccountAnalytcsImp;
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
import cm.aptoide.pt.v8engine.util.StoreUtils;
import cm.aptoide.pt.v8engine.view.recycler.DisplayableWidgetMapping;
import com.flurry.android.FlurryAgent;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import rx.Observable;
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

  public static void loadStores() {

    AptoideAccountManager.getUserRepos().subscribe(subscriptions -> {

      if (subscriptions.size() > 0) {
        for (Subscription subscription : subscriptions) {
          Store store = new Store();
          store.setDownloads(Long.parseLong(subscription.getDownloads()));
          store.setIconPath(subscription.getAvatarHd() != null ? subscription.getAvatarHd()
              : subscription.getAvatar());
          store.setStoreId(subscription.getId().longValue());
          store.setStoreName(subscription.getName());
          store.setTheme(subscription.getTheme());

          ((StoreAccessor) AccessorFactory.getAccessorFor(Store.class)).insert(store);
        }
      } else {
        addDefaultStore();
      }

      checkUpdates();
    }, e -> {
      Logger.e(TAG, e);
      //CrashReports.logException(e);
    });
  }

  private static void checkUpdates() {
    UpdateRepository repository = RepositoryFactory.getUpdateRepository();
    repository.getUpdates(true)
        .first()
        .subscribe(updates -> Logger.d(TAG, "updates are up to date now"), throwable -> {
          Logger.e(TAG, throwable);
          CrashReports.logException(throwable);
        });
  }

  public static void loadUserData() {
    loadStores();
    regenerateUserAgent();
  }

  private static void regenerateUserAgent() {
    SecurePreferences.setUserAgent(AptoideUtils.NetworkUtils.getDefaultUserAgent(
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(), getContext()),
        new UserData() {
          @Override public String getUserEmail() {
            return AptoideAccountManager.getUserEmail();
          }
        }, AptoideUtils.Core.getDefaultVername(), getConfiguration().getPartnerId()));
  }

  public static void clearUserData() {
    AccessorFactory.getAccessorFor(Store.class).removeAll();
    StoreUtils.subscribeStore(getConfiguration().getDefaultStore(), null, null);
    regenerateUserAgent();
  }

  private static void addDefaultStore() {
    StoreUtils.subscribeStore(getConfiguration().getDefaultStore(), getStoreMeta -> checkUpdates(),
        null);
  }

  /**
   * call after this instance onCreate()
   */
  protected void activateLogger() {
    Logger.setDBG(true);
  }

  @Override public void onCreate() {
    try {
      PRNGFixes.apply();
    } catch (Exception e) {
      Logger.e(TAG, "onCreate: " + e);
      CrashReports.logException(e);
    }
    long l = System.currentTimeMillis();
    AptoideUtils.setContext(this);
    fragmentProvider = createFragmentProvider();
    activityProvider = createActivityProvider();
    displayableWidgetMapping = createDisplayableWidgetMapping();

    //
    // super
    //
    super.onCreate();

    //if (BuildConfig.DEBUG) {
    //  RxJavaPlugins.getInstance().registerObservableExecutionHook(new RxJavaStackTracer());
    //}

    Database.initialize(this);

    generateAptoideUUID().subscribe();

    regenerateUserAgent();

    IntentFilter intentFilter = new IntentFilter(AptoideAccountManager.LOGIN);
    intentFilter.addAction(AptoideAccountManager.LOGOUT);
    this.registerReceiver(new BroadcastReceiver() {
      @Override public void onReceive(Context context, Intent intent) {
      }
    }, intentFilter);

    SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(this);
    Analytics.Lifecycle.Application.onCreate(this);
    Logger.setDBG(ManagerPreferences.isDebug() || cm.aptoide.pt.utils.BuildConfig.DEBUG);
    new FlurryAgent.Builder().withLogEnabled(false).build(this, "4KW7MPHSSG2Y7ZXHD36T");

    if (SecurePreferences.isFirstRun()) {
      createShortCut();
      PreferenceManager.setDefaultValues(this, R.xml.settings, false);
      if (AptoideAccountManager.isLoggedIn() && ManagerPreferences.isFirstRunV7()) {
        AptoideAccountManager.removeLocalAccount();
      }
      loadInstalledApps().doOnNext(o -> {
        if (AptoideAccountManager.isLoggedIn()) {

          if (!SecurePreferences.isUserDataLoaded()) {
            loadUserData();
            SecurePreferences.setUserDataLoaded();
          }
        } else {
          generateAptoideUUID().subscribe(success -> addDefaultStore());
        }
        SecurePreferences.setFirstRun(false);
      }).subscribe();

      // load picture, name and email
      AptoideAccountManager.refreshAndSaveUserInfoData().subscribe(userData -> {
        Logger.v(TAG, "hello " + userData.getUsername());
      }, e -> {
        Logger.e(TAG, e);
      });
    } else {
      loadInstalledApps().subscribe();
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

    final DownloadAccessor downloadAccessor = AccessorFactory.getAccessorFor(Download.class);
    FileManager fileManager = FileManager.build();
    AptoideDownloadManager.getInstance()
        .init(this, new DownloadNotificationActionsActionsInterface(),
            new DownloadManagerSettingsI(), downloadAccessor, CacheHelper.build(),
            new FileUtils(action -> Analytics.File.moveFile(action)), new TokenHttpClient(
                new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(), this),
                new UserData() {
                  @Override public String getUserEmail() {
                    return AptoideAccountManager.getUserEmail();
                  }
                }, getConfiguration().getPartnerId()),
            new DownloadAnalytics(Analytics.getInstance()));

    fileManager.purgeCache()
        .subscribe(cleanedSize -> Logger.d(TAG,
            "cleaned size: " + AptoideUtils.StringU.formatBytes(cleanedSize, false)), throwable -> {
          Logger.e(TAG, throwable);
          CrashReports.logException(throwable);
        });
    // setupCurrentActivityListener();

    //if (BuildConfig.DEBUG) {
    //  setupStrictMode();
    //  Logger.w(TAG, "StrictMode setup")

    // this will trigger the migration if needed
    // FIXME: 24/08/16 sithengineer the following line should be removed when no more SQLite -> Realm migration is needed
    SQLiteDatabase db = new SQLiteDatabaseHelper(this).getWritableDatabase();
    db.close();

    ABTestManager.getInstance()
        .initialize(new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
            this).getAptoideClientUUID())
        .subscribe(success -> {
        }, throwable -> {
          Logger.d(TAG, "An error has occurred when initializing the ABTestManager");
          CrashReports.logException(throwable);
        });

    AptoideAccountManager.setAnalytics(new AccountAnalytcsImp());
    Logger.d(TAG, "onCreate took " + (System.currentTimeMillis() - l) + " millis.");
  }

  @Override protected TokenInvalidator getTokenInvalidator() {
    return AptoideAccountManager::invalidateAccessToken;
  }

  protected void setupCrashReports(boolean isDisabled) {
    CrashReports.setup(AptoideCrashLogger.getInstance().setup(this, isDisabled));
  }

  protected FragmentProvider createFragmentProvider() {
    return new FragmentProviderImpl();
  }

  protected ActivityProvider createActivityProvider() {
    return new ActivityProviderImpl();
  }

  protected DisplayableWidgetMapping createDisplayableWidgetMapping() {
    return DisplayableWidgetMapping.getInstance();
  }

  Observable<String> generateAptoideUUID() {
    return Observable.fromCallable(
        () -> new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
            this).getAptoideClientUUID()).subscribeOn(Schedulers.computation());
  }

  //
  // Strict Mode
  //

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

  public void createShortCut() {
    Intent shortcutIntent = new Intent(this, MainActivityFragment.class);
    shortcutIntent.setAction(Intent.ACTION_MAIN);
    Intent intent = new Intent();
    intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
    intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Aptoide");
    intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
        Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_launcher));
    intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
    getApplicationContext().sendBroadcast(intent);
  }
}
