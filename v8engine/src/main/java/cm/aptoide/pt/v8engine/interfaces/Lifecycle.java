/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 12/05/2016.
 */

package cm.aptoide.pt.v8engine.interfaces;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by sithengineer on 12/05/16.
 */
public interface Lifecycle {

	@LayoutRes
	int getContentViewId();

	/**
	 * Bind needed views.
	 */
	void bindViews(@Nullable View view);

	void loadExtras(@Nullable Bundle extras);

	/**
	 * Setup previously binded views.
	 */
	void setupViews();

	/**
	 * Setup the toolbar, if present.
	 */
	void setupToolbar();
}
