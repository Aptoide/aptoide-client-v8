/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 27/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.content.Context;

import cm.aptoide.pt.actions.PermissionRequest;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.install.InstallManager;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;
import rx.Observable;

/**
 * Created by sithengineer on 14/06/16.
 */
public class RollbackDisplayable extends DisplayablePojo<Rollback> {

	private InstallManager installManager;
	private Download download;

	public RollbackDisplayable() { }

	public RollbackDisplayable(Rollback pojo) {
		super(pojo);
	}

	public RollbackDisplayable(Rollback pojo, boolean fixedPerLineCount) {
		super(pojo, fixedPerLineCount);
	}

	@Override
	public Type getType() {
		return Type.ROLLBACK;
	}

	@Override
	public int getViewLayout() {
		return R.layout.rollback_row;
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
}
