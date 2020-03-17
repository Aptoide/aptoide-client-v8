package cm.aptoide.pt.install;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.ads.MinimalAdMapper;
import cm.aptoide.pt.app.CampaignAnalytics;
import cm.aptoide.pt.app.migration.AppcMigrationManager;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.RoomStoredMinimalAdPersistence;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.database.room.RoomInstalled;
import cm.aptoide.pt.database.room.RoomStoredMinimalAd;
import cm.aptoide.pt.dataprovider.ads.AdNetworkUtils;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.repository.RepositoryFactory;
import cm.aptoide.pt.root.RootAvailabilityManager;
import cm.aptoide.pt.updates.UpdateRepository;
import cm.aptoide.pt.util.ReferrerUtils;
import cm.aptoide.pt.utils.AptoideUtils;
import javax.inject.Inject;
import rx.Completable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class InstalledIntentService extends IntentService {

  private static final String TAG = InstalledIntentService.class.getName();
  @Inject InstallAnalytics installAnalytics;
  @Inject CampaignAnalytics campaignAnalytics;
  @Inject AppcMigrationManager appcMigrationManager;
  @Inject RoomStoredMinimalAdPersistence roomStoredMinimalAdPersistence;
  private SharedPreferences sharedPreferences;
  private UpdateRepository updatesRepository;
  private CompositeSubscription subscriptions;
  private InstallManager installManager;
  private RootAvailabilityManager rootAvailabilityManager;
  private MinimalAdMapper adMapper;
  private PackageManager packageManager;

  public InstalledIntentService() {
    super("InstalledIntentService");
  }

  @Override public void onCreate() {
    super.onCreate();
    ((AptoideApplication) getApplicationContext()).getApplicationComponent()
        .inject(this);
    adMapper = new MinimalAdMapper();
    sharedPreferences =
        ((AptoideApplication) getApplicationContext()).getDefaultSharedPreferences();

    final SharedPreferences sharedPreferences =
        ((AptoideApplication) getApplicationContext()).getDefaultSharedPreferences();
    updatesRepository = RepositoryFactory.getUpdateRepository(this, sharedPreferences);
    subscriptions = new CompositeSubscription();
    installManager = ((AptoideApplication) getApplicationContext()).getInstallManager();
    rootAvailabilityManager =
        ((AptoideApplication) getApplicationContext()).getRootAvailabilityManager();
    packageManager = getPackageManager();
  }

  @Override protected void onHandleIntent(Intent intent) {
    if (intent != null) {
      final String action = intent.getAction();
      final String packageName = intent.getData()
          .getEncodedSchemeSpecificPart();

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

  protected void onPackageAdded(String packageName) {
    Logger.getInstance()
        .d(TAG, "Package added: " + packageName);

    PackageInfo packageInfo = databaseOnPackageAdded(packageName);
    checkAndBroadcastReferrer(packageName);
    sendInstallEvent(packageName, packageInfo);
    sendCampaignConversion(packageName, packageInfo);
    appcMigrationManager.persistCandidate(packageName);
  }

  protected void onPackageReplaced(String packageName) {
    Logger.getInstance()
        .d(TAG, "Packaged replaced: " + packageName);
    PackageInfo packageInfo = databaseOnPackageReplaced(packageName);
    sendInstallEvent(packageName, packageInfo);
    sendCampaignConversion(packageName, packageInfo);
  }

  protected void onPackageRemoved(String packageName) {
    Logger.getInstance()
        .d(TAG, "Packaged removed: " + packageName);
    sendUninstallEvent(packageName);
    databaseOnPackageRemoved(packageName);
  }

  private PackageInfo databaseOnPackageAdded(String packageName) {
    PackageInfo packageInfo = AptoideUtils.SystemU.getPackageInfo(packageName, getPackageManager());

    if (checkAndLogNullPackageInfo(packageInfo, packageName)) {
      return packageInfo;
    }
    RoomInstalled installed = new RoomInstalled(packageInfo, packageManager);
    installManager.onAppInstalled(installed)
        .subscribe(() -> {
        }, throwable -> CrashReport.getInstance()
            .log(throwable));
    return packageInfo;
  }

  private void checkAndBroadcastReferrer(String packageName) {
    Subscription unManagedSubscription = roomStoredMinimalAdPersistence.get(packageName)
        .observeOn(Schedulers.io())
        .flatMapCompletable(storedMinimalAd -> {
          if (storedMinimalAd != null) {
            return knockCpi(packageName, roomStoredMinimalAdPersistence, storedMinimalAd);
          } else {
            return null;
          }
        })
        .subscribe(__ -> { /* do nothing */ }, err -> {
          CrashReport.getInstance()
              .log(err);
        });

    subscriptions.add(unManagedSubscription);
  }

  private void sendInstallEvent(String packageName, PackageInfo packageInfo) {
    if (packageInfo != null) {
      installAnalytics.installCompleted(packageName, packageInfo.versionCode,
          rootAvailabilityManager.isRootAvailable()
              .toBlocking()
              .value(), ManagerPreferences.allowRootInstallation(sharedPreferences));
      return;
    }
    // information about the package is null so we don't broadcast an event
    reportPackageInfoNullEvent();
  }

  private void sendUninstallEvent(String packageName) {
    installAnalytics.uninstallCompleted(packageName);
  }

  private void reportPackageInfoNullEvent() {
    CrashReport.getInstance()
        .log(new NullPointerException("PackageInfo is null."));
  }

  private PackageInfo databaseOnPackageReplaced(String packageName) {
    final Update update = updatesRepository.get(packageName)
        .first()
        .doOnError(throwable -> {
          CrashReport.getInstance()
              .log(throwable);
        })
        .onErrorReturn(throwable -> null)
        .toBlocking()
        .first();

    PackageInfo packageInfo = AptoideUtils.SystemU.getPackageInfo(packageName, getPackageManager());

    if (checkAndLogNullPackageInfo(packageInfo, packageName)) {
      return packageInfo;
    }

    installManager.onUpdateConfirmed(new RoomInstalled(packageInfo, packageManager))
        .andThen(updatesRepository.remove(update))
        .subscribe(() -> Logger.getInstance()
                .d(TAG, "databaseOnPackageReplaced: " + packageName),
            throwable -> CrashReport.getInstance()
                .log(throwable));
    return packageInfo;
  }

  private void databaseOnPackageRemoved(String packageName) {
    installManager.onAppRemoved(packageName)
        .andThen(Completable.fromAction(() -> updatesRepository.remove(packageName)))
        .subscribe(() -> Logger.getInstance()
                .d(TAG, "databaseOnPackageRemoved: " + packageName),
            throwable -> CrashReport.getInstance()
                .log(throwable));
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

  private Completable knockCpi(String packageName,
      RoomStoredMinimalAdPersistence roomStoredMinimalAdPersistence,
      RoomStoredMinimalAd storedMinimalAd) {
    return Completable.fromCallable(() -> {
      ReferrerUtils.broadcastReferrer(packageName, storedMinimalAd.getReferrer(),
          getApplicationContext());
      AdNetworkUtils.knockCpi(adMapper.map(storedMinimalAd));
      roomStoredMinimalAdPersistence.remove(storedMinimalAd);
      return null;
    });
  }

  private void sendCampaignConversion(String packageName, PackageInfo packageInfo) {
    if (packageInfo != null) {
      campaignAnalytics.convertCampaignEvent(packageName, packageInfo.versionCode);
    } else {
      // information about the package is null so we don't broadcast an event
      reportPackageInfoNullEvent();
    }
  }
}
