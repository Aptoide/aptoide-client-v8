/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 02/06/2016.
 */

package cm.aptoide.pt.v8engine.fragment;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.custom.AptoideViewPager;

/**
 * Created by neuro on 01-06-2016.
 */
public abstract class BasePagerToolbarFragment extends BaseLoaderToolbarFragment {

  protected AptoideViewPager viewPager;
  protected FloatingActionButton floatingActionButton;

  @Override public void bindViews(View view) {
    super.bindViews(view);

    viewPager = (AptoideViewPager) view.findViewById(R.id.pager);
    floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fabAddStore);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    viewPager.clearOnPageChangeListeners();
    viewPager = null;
  }

  protected void setupViewPager() {
    final PagerAdapter pagerAdapter = createPagerAdapter();
    viewPager.setAdapter(pagerAdapter);
  }

  protected abstract PagerAdapter createPagerAdapter();
}
