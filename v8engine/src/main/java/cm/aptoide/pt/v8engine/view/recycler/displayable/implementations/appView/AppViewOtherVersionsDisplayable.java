/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView;

import cm.aptoide.pt.model.v7.store.GetStoreWidgets;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * Created by sithengineer on 04/05/16.
 */
public class AppViewOtherVersionsDisplayable extends DisplayablePojo<Object> {

	@Override
	public GetStoreWidgets.Type getName() {
		return null;
	}

	@Override
	public int getViewLayout() {
		return 0;
	}

	@Override
	public int getDefaultPerLineCount() {
		return 0;
	}
}
