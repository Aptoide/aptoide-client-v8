/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 26/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView;

import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by sithengineer on 06/05/16.
 */
public class AppViewInstallDisplayable extends AppViewDisplayable {

	@Getter @Setter private String cpdUrl;
	@Getter @Setter private String cpiUrl;

	public AppViewInstallDisplayable() {
	}

	public AppViewInstallDisplayable(GetApp getApp) {
		this(getApp, false, null);
	}

	public AppViewInstallDisplayable(GetApp getApp, String cpdUrl) {
		this(getApp, false, cpdUrl);
	}

	public AppViewInstallDisplayable(GetApp getApp, boolean fixedPerLineCount, String cpdUrl) {
		super(getApp, fixedPerLineCount);
		this.cpdUrl = cpdUrl;
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
