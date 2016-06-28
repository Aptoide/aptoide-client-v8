/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 28/06/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;

import cm.aptoide.pt.v8engine.fragment.BaseLoaderToolbarFragment;

/**
 * Created by sithengineer on 28/06/16.
 */
public class DescriptionFragment extends BaseLoaderToolbarFragment {

	private static final String APP_ID = "app_id";

	public DescriptionFragment() {
	}

	public DescriptionFragment newInstance(long appId) {
		DescriptionFragment fragment = new DescriptionFragment();
		Bundle args = new Bundle();
		args.putLong(APP_ID, appId);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	protected int getViewToShowAfterLoadingId() {
		return 0;
	}

	@Override
	public void load(boolean refresh) {

	}

	@Override
	public int getContentViewId() {
		return 0;
	}
}
