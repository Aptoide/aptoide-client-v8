/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.install;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import cm.aptoide.pt.crashreports.CrashReports;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.StoreMinimalAdAccessor;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.database.realm.StoredMinimalAd;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.dataprovider.ws.v2.aptwords.GetAdsRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.repository.InstalledRepository;
import cm.aptoide.pt.v8engine.repository.RollbackRepository;
import cm.aptoide.pt.v8engine.repository.UpdateRepository;
import cm.aptoide.pt.v8engine.util.referrer.ReferrerUtils;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by neuro on 24-05-2016.
 */
public class InstalledBroadcastReceiver extends BroadcastReceiver {

  private static final String TAG = "InstalledReceiver";
  private RollbackRepository repository;
  private InstalledRepository installedRepository;
  private UpdateRepository updatesRepository;

  @Override public void onReceive(Context context, Intent intent) {
    repository = new RollbackRepository(AccessorFactory.getAccessorFor(Rollback.class));
    installedRepository = new InstalledRepository(AccessorFactory.getAccessorFor(Installed.class));
    updatesRepository = new UpdateRepository(AccessorFactory.getAccessorFor(Update.class),
        AccessorFactory.getAccessorFor(Store.class));

    String action = intent.getAction();
    String packageName = intent.getData().getEncodedSchemeSpecificPart();

    confirmAction(packageName, action);

    if (!intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED) && intent.getBooleanExtra(
        Intent.EXTRA_REPLACING, false)) {
      return;
    }
    Logger.d(TAG, "Action : " + action);

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

  protected void onPackageAdded(String packageName) {
    Logger.d(TAG, "Package added: " + packageName);

    //Rollback rollback = DeprecatedDatabase.RollbackQ.get(realm, packageName, Rollback.Action.INSTALL);
    //if(rollback != null) {
    //	String trustedBadge = rollback.getTrustedBadge();
    //	Analytics.ApplicationInstall.installed(packageName, trustedBadge);
    //}

    databaseOnPackageAdded(packageName);
    checkAndBroadcastReferrer(packageName);
  }

  private void checkAndBroadcastReferrer(String packageName) {
    //StoredMinimalAd storedMinimalAd = DeprecatedDatabase.ReferrerQ.get(packageName, realm);
    //if (storedMinimalAd != null) {
    //  ReferrerUtils.broadcastReferrer(packageName, storedMinimalAd.getReferrer());
    //  DataproviderUtils.AdNetworksUtils.knockCpi(storedMinimalAd);
    //  DeprecatedDatabase.delete(storedMinimalAd, realm);
    //} else {
    //  GetAdsRequest.ofSecondInstall(packageName)
    //      .observe()
    //      .map(getAdsResponse -> MinimalAd.from(getAdsResponse.getAds().get(0)))
    //      .observeOn(AndroidSchedulers.mainThread())
    //      .doOnNext(
    //          minimalAd -> ReferrerUtils.extractReferrer(minimalAd, ReferrerUtils.RETRIES, true))
    //      .onErrorReturn(throwable1 -> new MinimalAd())
    //      .subscribe();
    //}

    StoreMinimalAdAccessor storeMinimalAdAccessor =
        AccessorFactory.getAccessorFor(StoredMinimalAd.class);
    Subscription unManagedSubscription =
        storeMinimalAdAccessor.get(packageName).subscribe(storeMinimalAd -> {
          if (storeMinimalAd != null) {
            ReferrerUtils.broadcastReferrer(packageName, storeMinimalAd.getReferrer());
            DataproviderUtils.AdNetworksUtils.knockCpi(storeMinimalAd);
            storeMinimalAdAccessor.remove(storeMinimalAd);
          } else {
            GetAdsRequest.ofSecondInstall(packageName,
                new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
                    DataProvider.getContext()).getAptoideClientUUID(),
                DataproviderUtils.AdNetworksUtils.isGooglePlayServicesAvailable())
                .observe()
                .map(getAdsResponse -> MinimalAd.from(getAdsResponse.getAds().get(0)))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(
                    minimalAd -> ReferrerUtils.extractReferrer(minimalAd, ReferrerUtils.RETRIES,
                        true))
                .onErrorReturn(throwable1 -> new MinimalAd())
                .subscribe();
          }
        }, err -> {
          Logger.e(TAG, err);
          CrashReports.logException(err);
        });
  }

  protected void onPackageReplaced(String packageName) {
    Logger.d(TAG, "Packaged replaced: " + packageName);
    databaseOnPackageReplaced(packageName);
  }

  protected void onPackageRemoved(String packageName) {
    Logger.d(TAG, "Packaged removed: " + packageName);
    databaseOnPackageRemoved(packageName);
  }

  private void databaseOnPackageAdded(String packageName) {
    PackageInfo packageInfo = AptoideUtils.SystemU.getPackageInfo(packageName);

    if (checkAndLogNullPackageInfo(packageInfo, packageName)) {
      return;
    }

    //DeprecatedDatabase.save(new Installed(packageInfo), realm);
    installedRepository.insert(new Installed(packageInfo));

    //Rollback rollback = DeprecatedDatabase.RollbackQ.get(realm, packageName, Rollback.Action.INSTALL);
    //if (rollback != null) {
    //	confirmAction(packageName, Rollback.Action.INSTALL);
    //}
  }

  private void databaseOnPackageReplaced(String packageName) {
    //Update update = DeprecatedDatabase.UpdatesQ.get(packageName, realm);
    //if (update != null && update.getPackageName() != null && update.getTrustedBadge() != null) {
    //  Analytics.ApplicationInstall.replaced(packageName, update.getTrustedBadge());
    //}
    //
    //PackageInfo packageInfo = AptoideUtils.SystemU.getPackageInfo(packageName);
    //if (checkAndLogNullPackageInfo(packageInfo)) {
    //  return;
    //}
    //
    //if (update != null) {
    //  if (packageInfo.versionCode >= update.getVersionCode()) {
    //    DeprecatedDatabase.delete(update, realm);
    //  }
    //}
    //
    //DeprecatedDatabase.save(new Installed(packageInfo), realm);

    Subscription unManagedSubscription = updatesRepository.get(packageName).subscribe(update -> {
      if (update != null && update.getPackageName() != null && update.getTrustedBadge() != null) {
        Analytics.ApplicationInstall.replaced(packageName, update.getTrustedBadge());
      }

      PackageInfo packageInfo = AptoideUtils.SystemU.getPackageInfo(packageName);

      if (checkAndLogNullPackageInfo(packageInfo, packageName)) {
        return;
      }

      if (update != null) {
        if (packageInfo.versionCode >= update.getVersionCode()) {
          updatesRepository.remove(update);
        }
      }

      installedRepository.insert(new Installed(packageInfo));
    }, err -> {
      Logger.e(TAG, err);
      CrashReports.logException(err);
    });

    //confirmAction(packageName, Rollback.Action.UPDATE);
  }

  /**
   * @param packageInfo packageInfo.
   * @return true if packageInfo is null, false otherwise.
   */
  private boolean checkAndLogNullPackageInfo(PackageInfo packageInfo, String packageName) {
    if (packageInfo == null) {
      CrashReports.logException(
          new IllegalArgumentException("PackageName null for package " + packageName));
      return true;
    } else {
      return false;
    }
  }

  private void databaseOnPackageRemoved(String packageName) {
    //DeprecatedDatabase.InstalledQ.delete(packageName, realm);
    //DeprecatedDatabase.UpdatesQ.delete(packageName, realm);

    installedRepository.remove(packageName);
    updatesRepository.remove(packageName);

    //Rollback rollback = DeprecatedDatabase.RollbackQ.get(realm, packageName, Rollback.Action.DOWNGRADE);
    //if (rollback != null) {
    //	confirmAction(packageName, Rollback.Action.DOWNGRADE);
    //	Analytics.ApplicationInstall.downgraded(packageName, rollback.getTrustedBadge());
    //} else {
    //	rollback = DeprecatedDatabase.RollbackQ.get(realm, packageName, Rollback.Action.UNINSTALL);
    //	if (rollback != null) {
    //		confirmAction(packageName, Rollback.Action.UNINSTALL);
    //	}
    //}
  }

  private void confirmAction(String packageName, String action) {
    repository.getNotConfirmedRollback(packageName)
        .first()
        .filter(rollback -> shouldConfirmRollback(rollback, action))
        .subscribe(rollback -> {
          repository.confirmRollback(rollback);
        }, throwable -> throwable.printStackTrace());
  }
}
