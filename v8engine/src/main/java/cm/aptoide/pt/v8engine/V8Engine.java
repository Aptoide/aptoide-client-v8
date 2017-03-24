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
import cm.aptoide.accountmanager.AccountFactory;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.CredentialsValidator;
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
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreMetaRequest;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.PRNGFixes;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecureCoderDecoder;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.spotandshareandroid.ShareApps;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.FileUtils;
import cm.aptoide.pt.utils.SecurityUtils;
import cm.aptoide.pt.v8engine.account.AndroidAccountDataPersist;
import cm.aptoide.pt.v8engine.account.BaseBodyInterceptorFactory;
import cm.aptoide.pt.v8engine.account.DatabaseStoreDataPersist;
import cm.aptoide.pt.v8engine.account.SocialAccountFactory;
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
import cm.aptoide.pt.v8engine.preferences.AdultContent;
import cm.aptoide.pt.v8engine.preferences.Preferences;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.util.StoreCredentialsProviderImpl;
import cm.aptoide.pt.v8engine.util.StoreUtilsProxy;
import cm.aptoide.pt.v8engine.view.MainActivity;
import cm.aptoide.pt.v8engine.view.recycler.DisplayableWidgetMapping;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

import static com.google.android.gms.auth.api.Auth.GOOGLE_SIGN_IN_API;

/**
 * Created by neuro on 14-04-2016.
 */
public abstract class V8Engine extends DataProvider {

  private static final String TAG = V8Engine.class.getName();

  @Getter private static FragmentProvider fragmentProvider;
  @Getter private static ActivityProvider activityProvider;
  @Getter private static DisplayableWidgetMapping displayableWidgetMapping;
  @Setter @Getter private static boolean autoUpdateWasCalled = false;
  @Getter @Setter private static ShareApps shareApps;

  private AptoideAccountManager accountManager;
  private BodyInterceptor<BaseBody> baseBodyInterceptor;
  private Preferences preferences;
  private cm.aptoide.pt.v8engine.preferences.SecurePreferences securePreferences;
  private SecureCoderDecoder secureCodeDecoder;
  private AdultContent adultContent;
  private AptoideClientUUID aptoideClientUUID;

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
    shareApps = new ShareApps(new SpotAndShareAnalytics());

    //
    // do not erase this code. it is useful to figure out when someone forgot to attach an error handler when subscribing and the app
    // is crashing in Rx without a proper stack trace
    //
    //if (BuildConfig.DEBUG) {
    //  RxJavaPlugins.getInstance().registerObservableExecutionHook(new RxJavaStackTracer());
    //}

    Database.initialize(this);

    generateAptoideUuid().observeOn(Schedulers.computation())
        .andThen(regenerateUserAgent(accountManager))
        .andThen(initAbTestManager())
        .andThen(prepareApp().onErrorComplete(err -> {
          // in case we have an error preparing the app, log that error and continue
          CrashReport.getInstance().log(err);
          return true;
        }))
        .andThen(discoverAndSaveInstalledApps())
        .subscribe(() -> { /* do nothing */}, error -> CrashReport.getInstance().log(error));

    // this will trigger the migration if needed
    SQLiteDatabase db = new SQLiteDatabaseHelper(this).getWritableDatabase();
    db.close();

    SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(this);
    Analytics.LocalyticsSessionControl.firstSession(sPref);
    Analytics.Lifecycle.Application.onCreate(this);
    Logger.setDBG(ManagerPreferences.isDebug() || BuildConfig.DEBUG);
    new FlurryAgent.Builder().withLogEnabled(false).build(this, BuildConfig.FLURRY_KEY);

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
            new TokenHttpClient(getAptoideClientUUID(), () -> accountManager.getAccountEmail(),
                getConfiguration().getPartnerId(), accountManager).customMake(),
            new DownloadAnalytics(Analytics.getInstance()));

    fileManager.purgeCache()
        .subscribe(cleanedSize -> Logger.d(TAG,
            "cleaned size: " + AptoideUtils.StringU.formatBytes(cleanedSize, false)), throwable -> {
          CrashReport.getInstance().log(throwable);
        });

    Logger.d(TAG, "onCreate took " + (System.currentTimeMillis() - l) + " millis.");
  }

  @Override protected TokenInvalidator getTokenInvalidator() {
    return new TokenInvalidator() {
      @Override public Single<String> invalidateAccessToken() {
        return accountManager.refreshToken()
            .andThen(accountManager.getAccountAsync())
            .map(account -> account.getToken());
      }
    };
  }

  public AptoideClientUUID getAptoideClientUUID() {
    if (aptoideClientUUID == null) {
      aptoideClientUUID =
          new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(), this);
    }
    return aptoideClientUUID;
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

  private Completable generateAptoideUuid() {
    return Completable.fromAction(() -> getAptoideClientUUID().getUniqueIdentifier())
        .subscribeOn(Schedulers.newThread());
  }

  private Completable regenerateUserAgent(final AptoideAccountManager accountManager) {
    return Completable.fromAction(() -> {
      final String userAgent = AptoideUtils.NetworkUtils.getDefaultUserAgent(getAptoideClientUUID(),
          () -> accountManager.getAccountEmail(), AptoideUtils.Core.getDefaultVername(),
          getConfiguration().getPartnerId());
      SecurePreferences.setUserAgent(userAgent);
    }).subscribeOn(Schedulers.newThread());
  }

  private Completable initAbTestManager() {
    return Completable.defer(() -> ABTestManager.getInstance()
        .initialize(getAptoideClientUUID().getUniqueIdentifier())
        .toCompletable());
  }

  private Completable prepareApp() {
    return accountManager.accountStatus().first().toSingle().flatMapCompletable(account -> {
      if (SecurePreferences.isFirstRun()) {

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        //Completable prepare;
        //if (account.isLoggedIn() && ManagerPreferences.isFirstRunV7()) {
        //  prepare = accountManager.logout();
        //} else{
          // load picture, name and email
          //prepare = setupFirstRun().andThen(
          //    Completable.merge(accountManager.syncCurrentAccount(), createShortcut()));
        //}
        //return prepare;

        return setupFirstRun().andThen(
            Completable.merge(accountManager.syncCurrentAccount(), createShortcut()));
      }

      return Completable.complete();
    });
  }

  public AptoideAccountManager getAccountManager() {
    if (accountManager == null) {

      final DatabaseStoreDataPersist dataPersist =
          new DatabaseStoreDataPersist(AccessorFactory.getAccessorFor(Store.class),
              new DatabaseStoreDataPersist.DatabaseStoreMapper());

      final BaseBodyInterceptorFactory bodyInterceptorFactory =
          new BaseBodyInterceptorFactory(getAptoideClientUUID(), getPreferences(),
              getSecurePreferences());

      final AccountFactory accountFactory = new AccountFactory(getAptoideClientUUID(),
          new SocialAccountFactory(this,
              new GoogleApiClient.Builder(this).addApi(GOOGLE_SIGN_IN_API,
                  new GoogleSignInOptions.Builder(
                      GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
                      .requestScopes(new Scope("https://www.googleapis.com/auth/contacts.readonly"))
                      .requestScopes(new Scope(Scopes.PROFILE))
                      .requestServerAuthCode(BuildConfig.GMS_SERVER_ID)
                      .build()).build()));

      accountManager = new AptoideAccountManager(getAptoideClientUUID(), new AccountAnalytcs(),
          bodyInterceptorFactory, new CredentialsValidator(), accountFactory,
          new AndroidAccountDataPersist(getConfiguration().getAccountType(),
              AccountManager.get(this), dataPersist, accountFactory));
    }
    return accountManager;
  }

  // todo re-factor all this code to proper Rx
  private Completable setupFirstRun() {
    return Completable.defer(() -> Completable.fromCallable(() -> {
      SecurePreferences.setFirstRun(false);
      if (accountManager.isLoggedIn()) {

        if (!SecurePreferences.isUserDataLoaded()) {
          return regenerateUserAgent(accountManager).doOnCompleted(
              () -> SecurePreferences.setUserDataLoaded())
              .doOnError(err -> CrashReport.getInstance().log(err));
        }
      }

      final StoreCredentialsProviderImpl storeCredentials = new StoreCredentialsProviderImpl();

      StoreUtilsProxy proxy =
          new StoreUtilsProxy(accountManager, getBaseBodyInterceptor(), storeCredentials,
              AccessorFactory.getAccessorFor(Store.class));

      BaseRequestWithStore.StoreCredentials defaultStoreCredentials =
          storeCredentials.get(getConfiguration().getDefaultStore());

      return generateAptoideUuid().andThen(proxy.addDefaultStore(
          GetStoreMetaRequest.of(defaultStoreCredentials, getBaseBodyInterceptor()), accountManager,
          defaultStoreCredentials).andThen(refreshUpdates()).toObservable())
          .doOnError(err -> CrashReport.getInstance().log(err));
    }));
  }

  public BodyInterceptor<BaseBody> getBaseBodyInterceptor() {
    if (baseBodyInterceptor == null) {
      baseBodyInterceptor = new BaseBodyInterceptor(getAptoideClientUUID(), getAccountManager(),
          getAdultContent(getSecurePreferences()));
    }
    return baseBodyInterceptor;
  }

  public AdultContent getAdultContent(
      cm.aptoide.pt.v8engine.preferences.SecurePreferences securePreferences) {
    if (adultContent == null) {
      adultContent = new AdultContent(accountManager, getPreferences(), securePreferences);
    }
    return adultContent;
  }

  public cm.aptoide.pt.v8engine.preferences.SecurePreferences getSecurePreferences() {
    if (securePreferences == null) {
      securePreferences = new cm.aptoide.pt.v8engine.preferences.SecurePreferences(
          PreferenceManager.getDefaultSharedPreferences(this), getSecureCoderDecoder());
    }
    return securePreferences;
  }

  public SecureCoderDecoder getSecureCoderDecoder() {
    if (secureCodeDecoder == null) {
      secureCodeDecoder = new SecureCoderDecoder.Builder(this).create();
    }
    return secureCodeDecoder;
  }

  public Preferences getPreferences() {
    if (preferences == null) {
      preferences = new Preferences(PreferenceManager.getDefaultSharedPreferences(this));
    }
    return preferences;
  }

  public Completable createShortcut() {
    return Completable.defer(() -> {
      createAppShortcut();
      return null;
    });
  }

  private Completable discoverAndSaveInstalledApps() {
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
        .toCompletable();
  }

  private Completable refreshUpdates() {
    return RepositoryFactory.getUpdateRepository(DataProvider.getContext()).sync(true);
  }

  /**
   * Use {@link #createShortcut()} using a {@link Completable}
   */
  @Deprecated @Partners public void createShortCut() {
    createAppShortcut();
  }

  private void createAppShortcut() {
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
