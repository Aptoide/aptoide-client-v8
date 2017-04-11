/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 06/07/2016.
 */

package cm.aptoide.pt.v8engine.view.search;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import cm.aptoide.pt.v8engine.V8Engine;

/**
 * Created by neuro on 28-04-2016.
 */
public class SearchPagerAdapter extends FragmentStatePagerAdapter {

  private final String query;
  private final boolean hasSubscribedResults;
  private final boolean hasEverywhereResults;
  private String storeName;

  public SearchPagerAdapter(FragmentManager fm, String query, String storeName) {
    this(fm, query, false, false);
    this.storeName = storeName;
  }

  public SearchPagerAdapter(FragmentManager fm, String query, boolean hasSubscribedResults,
      boolean hasEverywhereResults) {
    super(fm);
    this.query = query;
    this.hasSubscribedResults = hasSubscribedResults;
    this.hasEverywhereResults = hasEverywhereResults;
  }

  @Override public Fragment getItem(int position) {
    if (storeName != null) {
      return V8Engine.getFragmentProvider().newSearchPagerTabFragment(query, storeName);
    } else {
      if (getCount() > 1) {
        if (position == 0) {
          return V8Engine.getFragmentProvider()
              .newSearchPagerTabFragment(query, true, getCount() > 1);
        } else if (position == 1) {
          return V8Engine.getFragmentProvider()
              .newSearchPagerTabFragment(query, false, getCount() > 1);
        } else {
          throw new IllegalArgumentException("SearchPagerAdapter should have 2 and only 2 pages!");
        }
      } else {
        if (hasSubscribedResults) {
          return V8Engine.getFragmentProvider()
              .newSearchPagerTabFragment(query, true, getCount() > 1);
        } else {
          return V8Engine.getFragmentProvider()
              .newSearchPagerTabFragment(query, false, getCount() > 1);
        }
      }
    }
  }

  @Override public int getCount() {
    if (storeName != null) {
      return 1;
    } else {
      int count = 0;

      if (hasSubscribedResults) {
        count++;
      }

      if (hasEverywhereResults) {
        count++;
      }

      return count;
    }
  }
}
