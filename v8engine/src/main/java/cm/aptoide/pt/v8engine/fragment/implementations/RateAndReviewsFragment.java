/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 05/07/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;

import cm.aptoide.pt.v8engine.fragment.BasePagerToolbarFragment;

/**
 * Created by sithengineer on 13/05/16.
 */
public class RateAndReviewsFragment extends BasePagerToolbarFragment {

	private static final String APP_ID = "app_id";

	public static RateAndReviewsFragment newInstance(long appId) {
		RateAndReviewsFragment fragment = new RateAndReviewsFragment();
		Bundle args = new Bundle();
		args.putLong(APP_ID, appId);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	protected PagerAdapter createPagerAdapter() {
		return null;
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
