package cm.aptoide.pt.v8engine.install;

import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.StoreMinimalAdAccessor;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.database.realm.StoredMinimalAd;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.dataprovider.ws.v7.analyticsbody.DownloadInstallAnalyticsBaseBody;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events.InstallEvent;
import cm.aptoide.pt.v8engine.repository.AdsRepository;
import cm.aptoide.pt.v8engine.repository.InstalledRepository;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.repository.RollbackRepository;
import cm.aptoide.pt.v8engine.repository.UpdateRepository;
import cm.aptoide.pt.v8engine.util.referrer.ReferrerUtils;
import rx.Completable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.subscriptions.CompositeSubscription;

public class InstalledIntentService extends IntentService {

  private static final String TAG = InstalledIntentService.class.getName();

  private AdsRepository adsRepository;
  private RollbackRepository repository;
  private InstalledRepository installedRepository;
  private UpdateRepository updatesRepository;
  private CompositeSubscription subscriptions;
  private Analytics analytics;

  public InstalledIntentService() {
    super("InstalledIntentService");
  }

  @Override public void onCreate() {
    super.onCreate();
    final AptoideAccountManager accountManager =
        ((V8Engine) getApplicationContext()).getAccountManager();
    adsRepository = new AdsRepository(
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
            DataProvider.getContext()), accountManager);
    repository = RepositoryFactory.getRollbackRepository();
    installedRepository = RepositoryFactory.getInstalledRepository();
    updatesRepository = RepositoryFactory.getUpdateRepository(this);

    subscriptions = new CompositeSubscription();
    analytics = Analytics.getInstance();
  }

  @Override protected void onHandleIntent(Intent intent) {
    if (intent != null) {
      final String action = intent.getAction();
      final String packageName = intent.getData().getEncodedSchemeSpecificPart();

      confirmAction(packageName, action);

      if (!TextUtils.equals(action, Intent.ACTION_PACKAGE_REPLACED) && intent.getBooleanExtra(
          Intent.EXTRA_REPLACING, false)) {
        // do nothing if its a replacement ongoing. we are only interested in
        // already replaced apps
        return;
      }

      switch (action) {
        case Intent.ACTION_PACKAGE_ADDED:
          onPackageAdded(packageName);
          break;
        case Intent.ACTION_PACKAGE_REPLACED:
          onPackageReplaced(packageName);
          break;
        case Intent.ACTION_PACKAGE_REMOVED:
          onPackageRemoved(packageName);
          break;
      }
    }
  }

  private void confirmAction(String packageName, String action) {
    repository.getNotConfirmedRollback(packageName)
        .first()
        .filter(rollback -> shouldConfirmRollback(rollback, action))
        .subscribe(rollback -> {
          repository.confirmRollback(rollback);
        }, throwable -> throwable.printStackTrace());
  }

  protected void onPackageAdded(String packageName) {
    Logger.d(TAG, "Package added: " + packageName);

    PackageInfo packageInfo = databaseOnPackageAdded(packageName);
    checkAndBroadcastReferrer(packageName);
    sendInstallEvent(packageName, packageInfo);
  }

  protected void onPackageReplaced(String packageName) {
    Logger.d(TAG, "Packaged replaced: " + packageName);
    PackageInfo packageInfo = databaseOnPackageReplaced(packageName);
    sendInstallEvent(packageName, packageInfo);
  }

  protected void onPackageRemoved(String packageName) {
    Logger.d(TAG, "Packaged removed: " + packageName);
    databaseOnPackageRemoved(packageName);
  }

  private boolean shouldConfirmRollback(Rollback rollback, String action) {
    return rollback != null && ((rollback.getAction().equals(Rollback.Action.INSTALL.name())
        && action.equals(Intent.ACTION_PACKAGE_ADDED))
        || (rollback.getAction()
        .equals(Rollback.Action.UNINSTALL.name()) && action.equals(Intent.ACTION_PACKAGE_REMOVED))
        || (rollback.getAction().equals(Rollback.Action.UPDATE.name()) && action.equals(
        Intent.ACTION_PACKAGE_REPLACED))
        || (rollback.getAction().equals(Rollback.Action.DOWNGRADE.name()) && action.equals(
        Intent.ACTION_PACKAGE_ADDED)));
  }

  private PackageInfo databaseOnPackageAdded(String packageName) {
    PackageInfo packageInfo = AptoideUtils.SystemU.getPackageInfo(packageName);

    if (checkAndLogNullPackageInfo(packageInfo, packageName)) {
      return packageInfo;
    }
    installedRepository.save(new Installed(packageInfo));
    return packageInfo;
  }

  private void checkAndBroadcastReferrer(String packageName) {
    StoreMinimalAdAccessor storeMinimalAdAccessor =
        AccessorFactory.getAccessorFor(StoredMinimalAd.class);
    Subscription unManagedSubscription =
        storeMinimalAdAccessor.get(packageName).flatMapCompletable(storeMinimalAd -> {
          if (storeMinimalAd != null) {
            return knockCpi(packageName, storeMinimalAdAccessor, storeMinimalAd);
          } else {
            return extractReferrer(packageName);
          }
        }).subscribe(__ -> { /* do nothing */ }, err -> {
          CrashReport.getInstance().log(err);
        });

    subscriptions.add(unManagedSubscription);
  }

  private void sendInstallEvent(String packageName, PackageInfo packageInfo) {
    if (packageInfo != null) {
      InstallEvent event =
          (InstallEvent) analytics.get(packageName + packageInfo.versionCode, InstallEvent.class);
      if (event != null) {
        event.setResultStatus(DownloadInstallAnalyticsBaseBody.ResultStatus.SUCC);
        analytics.sendEvent(event);
        return;
      }

      CrashReport.getInstance().log(new NullPointerException("Event is null."));
      return;
    }

    // information about the package is null so we don't broadcast an event
    CrashReport.getInstance().log(new NullPointerException("PackageInfo is null."));
  }

  private PackageInfo databaseOnPackageReplaced(String packageName) {
    final Update update = updatesRepository.get(packageName).doOnError(throwable -> {
      CrashReport.getInstance().log(throwable);
    }).onErrorReturn(throwable -> null).toBlocking().first();

    if (update != null && update.getPackageName() != null && update.getTrustedBadge() != null) {
      Analytics.ApplicationInstall.replaced(packageName, update.getTrustedBadge());
    }

    PackageInfo packageInfo = AptoideUtils.SystemU.getPackageInfo(packageName);

    if (checkAndLogNullPackageInfo(packageInfo, packageName)) {
      return packageInfo;
    }

    Action0 insertApp = () -> installedRepository.save(new Installed(packageInfo));

    if (update != null) {
      if (packageInfo.versionCode >= update.getVersionCode()) {
        // remove old update and on complete insert new app.
        updatesRepository.remove(update)
            .subscribe(insertApp, throwable -> CrashReport.getInstance().log(throwable));
      }
    } else {
      // sync call to insert
      insertApp.call();
    }

    return packageInfo;
  }

  private void databaseOnPackageRemoved(String packageName) {
    installedRepository.remove(packageName);
    updatesRepository.remove(packageName);
  }

  /**
   * @param packageInfo packageInfo.
   *
   * @return true if packageInfo is null, false otherwise.
   */
  private boolean checkAndLogNullPackageInfo(PackageInfo packageInfo, String packageName) {
    if (packageInfo == null) {
      CrashReport.getInstance()
          .log(new IllegalArgumentException("PackageName null for package " + packageName));
      return true;
    } else {
      return false;
    }
  }

  private Completable knockCpi(String packageName, StoreMinimalAdAccessor storeMinimalAdAccessor,
      StoredMinimalAd storeMinimalAd) {
    return Completable.fromCallable(() -> {
      ReferrerUtils.broadcastReferrer(packageName, storeMinimalAd.getReferrer());
      DataproviderUtils.AdNetworksUtils.knockCpi(storeMinimalAd);
      storeMinimalAdAccessor.remove(storeMinimalAd);
      return null;
    });
  }

  @NonNull private Completable extractReferrer(String packageName) {
    return adsRepository.getAdsFromSecondInstall(packageName)
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(minimalAd -> ReferrerUtils.extractReferrer(minimalAd, ReferrerUtils.RETRIES, true,
            adsRepository))
        .toCompletable();
  }
}
