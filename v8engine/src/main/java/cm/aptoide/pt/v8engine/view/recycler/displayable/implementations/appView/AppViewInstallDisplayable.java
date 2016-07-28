/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 26/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView;

import android.content.Context;

import cm.aptoide.pt.actions.PermissionRequest;
import cm.aptoide.pt.dataprovider.model.MinimalAd;
import cm.aptoide.pt.downloadmanager.DownloadServiceHelper;
import cm.aptoide.pt.model.v3.GetApkInfoJson;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.install.InstallManager;
import lombok.Getter;
import rx.Observable;

/**
 * Created by sithengineer on 06/05/16.
 */
public class AppViewInstallDisplayable extends AppViewDisplayable {

	@Getter private GetApkInfoJson.Payment payment;
	@Getter private MinimalAd minimalAd;
	private InstallManager installManager;
	private DownloadServiceHelper downloadManager;
	private long appId;
	private String packageName;
	private String storeName;

	public AppViewInstallDisplayable() {
	}

	public AppViewInstallDisplayable(InstallManager installManager, DownloadServiceHelper downloadManager, GetApp getApp, MinimalAd ad) {
		super(getApp, false);
		this.installManager = installManager;
		this.appId = getApp.getNodes().getMeta().getData().getId();
		this.packageName = getApp.getNodes().getMeta().getData().getPackageName();
		this.storeName = getApp.getNodes().getMeta().getData().getStore().getName();
		this.payment = getApp.getNodes().getMeta().getData().getPayment();
		this.minimalAd = ad;
	}

	public boolean isPaidApp() {
		return payment != null;
	}

	public boolean shouldCharge() {
		return payment.getStatus().equals("FAIL");
	}

	public Observable<Void> install(Context context) {
		return installManager.install(context, (PermissionRequest) context, appId);
	}

	public Observable<Void> uninstall(Context context) {
		return installManager.uninstall(context, packageName);
	}

	public Observable<Void> downgrade(Context context) {
		return Observable.concat(uninstall(context).ignoreElements(), install(context));
	}

	@Override
	public Type getType() {
		return Type.APP_VIEW_INSTALL;
	}

	@Override
	public int getViewLayout() {
		return R.layout.displayable_app_view_install;
	}
}
