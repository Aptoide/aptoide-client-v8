/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 25/08/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView;

import cm.aptoide.pt.annotation.Ignore;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;
import lombok.Setter;
import rx.functions.Action0;

/**
 * Created by sithengineer on 04/05/16.
 */
@Ignore
abstract class AppViewDisplayable extends DisplayablePojo<GetApp> {

	@Setter private Action0 onResumeAction;
	@Setter private Action0 onPauseAction;
	public AppViewDisplayable() {

	}

	public AppViewDisplayable(GetApp getApp) {
		super(getApp);
	}

	public AppViewDisplayable(GetApp getApp, boolean fixedPerLineCount) {
		super(getApp, fixedPerLineCount);
	}

	@Override public void onResume() {
		super.onResume();
		if (onResumeAction != null) {
			onResumeAction.call();
		}
	}

	@Override public void onPause() {
		if (onPauseAction != null) {
			onPauseAction.call();
		}
		super.onPause();
	}

}
