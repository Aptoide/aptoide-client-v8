/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 26/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView;

import android.content.Context;

import java.io.File;

import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.install.InstallManager;
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

	public AppViewInstallDisplayable() {
	}

	public AppViewInstallDisplayable(InstallManager installManager, GetApp getApp) {
		this(getApp, false, null);
		this.installManager = installManager;
	}

	public AppViewInstallDisplayable(InstallManager installManager, GetApp getApp, String cpdUrl) {
		this(getApp, false, cpdUrl);
		this.installManager = installManager;
	}

	public AppViewInstallDisplayable(GetApp getApp, boolean fixedPerLineCount, String cpdUrl) {
		super(getApp, fixedPerLineCount);
		this.cpdUrl = cpdUrl;
	}

	public Observable<Void> install(Context context, File file, String packageName) {
		return installManager.install(context, file, packageName);
	}

	public Observable<Void> uninstall(Context context, String packageName) {
		return installManager.uninstall(context, packageName);
	}

	public Observable<Void> downgrade(Context context, File installFile, String packageName) {
		return Observable.concat(uninstall(context, packageName),
				install(context, installFile, packageName));
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
