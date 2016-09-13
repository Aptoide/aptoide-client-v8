/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 25/08/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import cm.aptoide.pt.actions.PermissionRequest;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.InstalledAccessor;
import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.dataprovider.model.MinimalAd;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.install.Installer;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.interfaces.Payments;
import cm.aptoide.pt.v8engine.repository.RollbackRepository;
import lombok.Getter;
import rx.Observable;

/**
 * Created by sithengineer on 06/05/16.
 */
public class AppViewInstallDisplayable extends AppViewDisplayable {

	private static final String TAG = AppViewInstallDisplayable.class.getName();

	@Getter private boolean shouldInstall;
	@Getter private MinimalAd minimalAd;

	private RollbackRepository rollbackRepository;
	private Installer installManager;

	private long appId;
	private String packageName;
	private InstalledAccessor installedAccessor;

	public AppViewInstallDisplayable() {
		super();
	}

	public AppViewInstallDisplayable(Installer installManager, GetApp getApp, MinimalAd minimalAd,
			boolean shouldInstall,
			InstalledAccessor installedAccessor) {
		super(getApp);
		this.installManager = installManager;
		this.appId = getApp.getNodes().getMeta().getData().getId();
		this.packageName = getApp.getNodes().getMeta().getData().getPackageName();
		this.minimalAd = minimalAd;
		this.shouldInstall = shouldInstall;
		this.rollbackRepository =
				new RollbackRepository(AccessorFactory.getAccessorFor(Rollback.class));
		this.installedAccessor = installedAccessor;
	}

	public static AppViewInstallDisplayable newInstance(GetApp getApp, Installer installManager,
			MinimalAd minimalAd, boolean shouldInstall, InstalledAccessor installedAccessor) {
		return new AppViewInstallDisplayable(installManager, getApp, minimalAd, shouldInstall,
				installedAccessor);
	}

	public void buyApp(Context context, GetAppMeta.App app) {
		Fragment fragment = ((FragmentShower) context).getLastV4();
		if (Payments.class.isAssignableFrom(fragment.getClass())) {
			((Payments) fragment).buyApp(app);
		}
	}

	public Observable<Void> update(Context context) {
		return installManager.update(context, (PermissionRequest) context, appId);
	}

	public Observable<Void> install(Context context) {
		return installManager.install(context, (PermissionRequest) context, appId);
	}

	public Observable<Void> uninstall(Context context) {
		return installManager.uninstall(context, packageName);
	}

	public Observable<Void> downgrade(Context context) {
		return installManager.downgrade(context, (PermissionRequest) context, appId);
	}

	@Override
	public Type getType() {
		return Type.APP_VIEW_INSTALL;
	}

	@Override
	public int getViewLayout() {
		return R.layout.displayable_app_view_install;
	}

	@Override
	public void onResume() {
		super.onResume();
		Logger.i(TAG, "onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		Logger.i(TAG, "onPause");
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Logger.i(TAG, "onSaveInstanceState");
	}

	@Override
	public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		Logger.i(TAG, "onViewStateRestored");
	}

	public InstalledAccessor getInstalledAccessor() {
		return installedAccessor;
	}
}
