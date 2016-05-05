/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView;

import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * Created by sithengineer on 04/05/16.
 */
public abstract class AppViewDisplayable<T> extends DisplayablePojo<T> {

	@Override
	public int getDefaultPerLineCount() {
		return 1;
	}
}
