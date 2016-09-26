/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 02/06/2016.
 */

package cm.aptoide.pt.v8engine.fragment;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.custom.AptoideViewPager;

/**
 * Created by neuro on 01-06-2016.
 */
public abstract class BasePagerToolbarFragment extends BaseLoaderToolbarFragment {

  protected AptoideViewPager mViewPager;

  @Override public void onDestroyView() {
    super.onDestroyView();
    mViewPager = null;
  }

  @Override public void bindViews(View view) {
    super.bindViews(view);

    mViewPager = (AptoideViewPager) view.findViewById(R.id.pager);
  }

  protected void setupViewPager() {
    final PagerAdapter pagerAdapter = createPagerAdapter();
    mViewPager.setAdapter(pagerAdapter);
  }

  protected abstract PagerAdapter createPagerAdapter();
}
