/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 26/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView;

import android.content.Context;

import cm.aptoide.pt.actions.PermissionRequest;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.downloadmanager.DownloadServiceHelper;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.install.InstallManager;
import cm.aptoide.pt.v8engine.install.Installation;
import cm.aptoide.pt.v8engine.util.DownloadFactory;
import lombok.Getter;
import lombok.Setter;
import rx.Observable;

/**
 * Created by sithengineer on 06/05/16.
 */
public class AppViewInstallDisplayable extends AppViewDisplayable {

	@Getter @Setter private String cpdUrl;
	@Getter @Setter private String cpiUrl;
	private InstallManager installManager;
	private DownloadServiceHelper downloadManager;
	private Download download;

	public AppViewInstallDisplayable() {
	}

	public AppViewInstallDisplayable(InstallManager installManager, GetApp getApp, DownloadServiceHelper downloadManager) {
		this(getApp, false, null);
		this.installManager = installManager;
		this.downloadManager = downloadManager;
		this.download = new DownloadFactory().create(getApp.getNodes().getMeta().getData());
	}

	public AppViewInstallDisplayable(InstallManager installManager, GetApp getApp, String cpdUrl, DownloadServiceHelper downloadManager) {
		this(getApp, false, cpdUrl);
		this.installManager = installManager;
		this.download = new DownloadFactory().create(getApp.getNodes().getMeta().getData());
	}

	public AppViewInstallDisplayable(GetApp getApp, boolean fixedPerLineCount, String cpdUrl) {
		super(getApp, fixedPerLineCount);
		this.cpdUrl = cpdUrl;
		this.download = new DownloadFactory().create(getApp.getNodes().getMeta().getData());
	}

	public Observable<Void> install(Context context) {
		return installManager.install(context, (PermissionRequest) context, download.getAppId());
	}

	public Observable<Void> uninstall(Context context) {
		return installManager.uninstall(context, download.getFilesToDownload().get(0).getPackageName());
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
