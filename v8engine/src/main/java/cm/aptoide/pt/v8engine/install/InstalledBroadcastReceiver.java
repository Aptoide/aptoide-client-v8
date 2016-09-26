/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.install;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.util.Log;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.DeprecatedDatabase;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.database.realm.StoredMinimalAd;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.dataprovider.model.MinimalAd;
import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.dataprovider.ws.v2.aptwords.GetAdsRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.CrashReports;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.repository.RollbackRepository;
import cm.aptoide.pt.v8engine.util.referrer.ReferrerUtils;
import io.realm.Realm;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by neuro on 24-05-2016.
 */
public class InstalledBroadcastReceiver extends BroadcastReceiver {

  private static final String TAG = "InstalledReceiver";
  private Realm realm;
  private RollbackRepository repository;

  @Override public void onReceive(Context context, Intent intent) {
    loadRealm();

    repository = new RollbackRepository(AccessorFactory.getAccessorFor(Rollback.class));

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
        onPackageReplaced(packageName, context);
        break;
      case Intent.ACTION_PACKAGE_REMOVED:
        onPackageRemoved(packageName);
        break;
    }

    closeRealm();
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

  private void loadRealm() {
    if (realm == null) {
      realm = DeprecatedDatabase.get();
    }
  }

  private void closeRealm() {
    if (realm != null) {
      realm.close();
    }
  }

  protected void onPackageAdded(String packageName) {
    Log.d(TAG, "Package added: " + packageName);

    //Rollback rollback = DeprecatedDatabase.RollbackQ.get(realm, packageName, Rollback.Action.INSTALL);
    //if(rollback != null) {
    //	String trustedBadge = rollback.getTrustedBadge();
    //	Analytics.ApplicationInstall.installed(packageName, trustedBadge);
    //}

    databaseOnPackageAdded(packageName);
    checkAndBroadcastReferrer(packageName);
  }

  private void checkAndBroadcastReferrer(String packageName) {
    StoredMinimalAd storedMinimalAd = DeprecatedDatabase.ReferrerQ.get(packageName, realm);
    if (storedMinimalAd != null) {
      ReferrerUtils.broadcastReferrer(packageName, storedMinimalAd.getReferrer());
      DataproviderUtils.AdNetworksUtils.knockCpi(storedMinimalAd);
      DeprecatedDatabase.delete(storedMinimalAd, realm);
    } else {
      GetAdsRequest.ofSecondInstall(packageName)
          .observe()
          .map(getAdsResponse -> MinimalAd.from(getAdsResponse.getAds().get(0)))
          .observeOn(AndroidSchedulers.mainThread())
          .doOnNext(
              minimalAd -> ReferrerUtils.extractReferrer(minimalAd, ReferrerUtils.RETRIES, true))
          .onErrorReturn(throwable1 -> new MinimalAd())
          .subscribe();
    }
  }

  protected void onPackageReplaced(String packageName, Context context) {
    Logger.d(TAG, "Packaged replaced: " + packageName);

    databaseOnPackageReplaced(packageName, context);
  }

  protected void onPackageRemoved(String packageName) {
    Logger.d(TAG, "Packaged removed: " + packageName);
    databaseOnPackageRemoved(packageName);
  }

  private void databaseOnPackageAdded(String packageName) {
    PackageInfo packageInfo = AptoideUtils.SystemU.getPackageInfo(packageName);

    checkAndLogNullPackageIngo(packageInfo);

    DeprecatedDatabase.save(new Installed(packageInfo), realm);

    //Rollback rollback = DeprecatedDatabase.RollbackQ.get(realm, packageName, Rollback.Action.INSTALL);
    //if (rollback != null) {
    //	confirmAction(packageName, Rollback.Action.INSTALL);
    //}
  }

  private void databaseOnPackageReplaced(String packageName, Context context) {
    Update update = DeprecatedDatabase.UpdatesQ.get(packageName, realm);

    if (update != null && update.getPackageName() != null && update.getTrustedBadge() != null) {
      Analytics.ApplicationInstall.replaced(packageName, update.getTrustedBadge());
    }

    PackageInfo packageInfo = AptoideUtils.SystemU.getPackageInfo(packageName);

    checkAndLogNullPackageIngo(packageInfo);

    if (update != null) {
      if (packageInfo.versionCode >= update.getVersionCode()) {
        DeprecatedDatabase.delete(update, realm);
      }
    }

    DeprecatedDatabase.save(new Installed(packageInfo), realm);

    //confirmAction(packageName, Rollback.Action.UPDATE);
  }

  private void checkAndLogNullPackageIngo(PackageInfo packageInfo) {
    if (packageInfo == null) {
      CrashReports.logException(new IllegalArgumentException("PackageName null!"));
    }
  }

  private void databaseOnPackageRemoved(String packageName) {
    DeprecatedDatabase.InstalledQ.delete(packageName, realm);
    DeprecatedDatabase.UpdatesQ.delete(packageName, realm);

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
