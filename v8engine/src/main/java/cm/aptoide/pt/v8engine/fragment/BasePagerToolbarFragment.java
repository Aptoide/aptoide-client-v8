/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 02/06/2016.
 */

package cm.aptoide.pt.v8engine.fragment;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import cm.aptoide.pt.v8engine.R;

/**
 * Created by neuro on 01-06-2016.
 */
public abstract class BasePagerToolbarFragment extends BaseLoaderToolbarFragment {

	protected ViewPager mViewPager;

	@Override
	public void bindViews(View view) {
		super.bindViews(view);

		mViewPager = (ViewPager) view.findViewById(R.id.pager);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mViewPager = null;
	}

	protected void setupViewPager() {
		final PagerAdapter pagerAdapter = createPagerAdapter();
		mViewPager.setAdapter(pagerAdapter);
	}

	protected abstract PagerAdapter createPagerAdapter();
}
