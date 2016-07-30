/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 29/07/2016.
 */

package cm.aptoide.pt.v8engine.install;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.util.Log;

import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.database.realm.StoredMinimalAd;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.dataprovider.model.MinimalAd;
import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.dataprovider.ws.v2.aptwords.GetAdsRequest;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.util.referrer.ReferrerUtils;
import io.realm.Realm;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by neuro on 24-05-2016.
 */
public class InstalledBroadcastReceiver extends BroadcastReceiver {

	private static final String TAG = "InstalledReceiver";
	private Realm realm;

	@Override
	public void onReceive(Context context, Intent intent) {
		loadRealm();

		String action = intent.getAction();
		String packageName = intent.getData().getEncodedSchemeSpecificPart();

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

		closeRealm();
	}

	private void loadRealm() {
		if (realm == null) {
			realm = Database.get();
		}
	}

	private void closeRealm() {
		if (realm != null) {
			realm.close();
		}
	}

	protected void onPackageAdded(String packageName) {
		Log.d(TAG, "Package added: " + packageName);

		databaseOnPackageAdded(packageName);
		checkAndBroadcastReferrer(packageName);
	}

	private void checkAndBroadcastReferrer(String packageName) {
		StoredMinimalAd storedMinimalAd = Database.ReferrerQ.get(packageName, realm);
		if (storedMinimalAd != null) {
			ReferrerUtils.broadcastReferrer(packageName, storedMinimalAd.getReferrer());
			DataproviderUtils.AdNetworksUtils.knockCpi(storedMinimalAd);
			Database.delete(storedMinimalAd, realm);
		} else {
			GetAdsRequest.ofSecondInstall(packageName)
					.observe()
					.map(getAdsResponse -> MinimalAd.from(getAdsResponse.getAds().get(0)))
					.observeOn(AndroidSchedulers.mainThread())
					.doOnNext(minimalAd -> ReferrerUtils.extractReferrer(minimalAd, ReferrerUtils.RETRIES, true)).onErrorReturn(throwable1 -> new MinimalAd())
					.subscribe();
		}
	}

	protected void onPackageReplaced(String packageName) {
		Log.d(TAG, "Packaged replaced: " + packageName);

		databaseOnPackageReplaced(packageName);
	}

	protected void onPackageRemoved(String packageName) {
		Log.d(TAG, "Packaged removed: " + packageName);

		databaseOnPackageRemoved(packageName);
	}

	private void databaseOnPackageAdded(String packageName) {
		PackageInfo packageInfo = AptoideUtils.SystemU.getPackageInfo(packageName);

		Database.save(new Installed(packageInfo, V8Engine.getContext().getPackageManager()), realm);

		Rollback rollback = Database.RollbackQ.get(packageName, Rollback.Action.INSTALL, realm);
		if (rollback != null) {
			confirmAction(packageName, Rollback.Action.INSTALL);
		}
	}

	private void databaseOnPackageReplaced(String packageName) {
		Update update = Database.UpdatesQ.get(packageName, realm);

		PackageInfo packageInfo = AptoideUtils.SystemU.getPackageInfo(packageName);
		if (update != null) {
			if (packageInfo.versionCode >= update.getVersionCode()) {
				Database.delete(update, realm);
			}
		}

		Installed installed = Database.InstalledQ.get(packageName, realm);

		if (installed != null) {
			confirmAction(packageName, Rollback.Action.UPDATE);
		}
	}

	private void databaseOnPackageRemoved(String packageName) {
		Database.InstalledQ.delete(packageName, realm);
		Database.UpdatesQ.delete(packageName, realm);

		Rollback rollback = Database.RollbackQ.get(packageName, Rollback.Action.DOWNGRADE, realm);
		if (rollback != null) {
			confirmAction(packageName, Rollback.Action.DOWNGRADE);
		} else {
			rollback = Database.RollbackQ.get(packageName, Rollback.Action.UNINSTALL, realm);
			if (rollback != null) {
				confirmAction(packageName, Rollback.Action.UNINSTALL);
			}
		}
	}

	private void confirmAction(String packageName, Rollback.Action action) {
		Rollback rollback = Database.RollbackQ.get(packageName, action, realm);
		if (rollback != null) {
			rollback.confirm(realm);
		}
	}
}
