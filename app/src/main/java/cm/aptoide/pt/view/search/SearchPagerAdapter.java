/*
 * Copyright (c) 2016.
 * Modified on 06/07/2016.
 */

package cm.aptoide.pt.view.search;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.NavigationTrackerPagerAdapterHelper;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;

import static cm.aptoide.pt.view.fragment.NavigationTrackFragment.SHOULD_REGISTER_VIEW;

/**
 * Created by neuro on 28-04-2016.
 */
public class SearchPagerAdapter extends FragmentStatePagerAdapter
    implements NavigationTrackerPagerAdapterHelper {

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
    Fragment fragment;
    if (storeName != null) {
      fragment = AptoideApplication.getFragmentProvider()
          .newSearchPagerTabFragment(query, storeName);
    } else {
      if (getCount() > 1) {
        if (position == 0) {
          fragment = AptoideApplication.getFragmentProvider()
              .newSearchPagerTabFragment(query, true, getCount() > 1);
        } else if (position == 1) {
          fragment = AptoideApplication.getFragmentProvider()
              .newSearchPagerTabFragment(query, false, getCount() > 1);
        } else {
          throw new IllegalArgumentException("SearchPagerAdapter should have 2 and only 2 pages!");
        }
      } else {
        if (hasSubscribedResults) {
          fragment = AptoideApplication.getFragmentProvider()
              .newSearchPagerTabFragment(query, true, getCount() > 1);
        } else {
          fragment = AptoideApplication.getFragmentProvider()
              .newSearchPagerTabFragment(query, false, getCount() > 1);
        }
      }
    }
    return setFragmentLogFlag(fragment);
  }

  private Fragment setFragmentLogFlag(Fragment fragment) {
    Bundle bundle = fragment.getArguments();
    if (bundle == null) {
      bundle = new Bundle();
    }
    bundle.putBoolean(SHOULD_REGISTER_VIEW, false);
    fragment.setArguments(bundle);
    return fragment;
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

  @Override public String getItemName(int position) {
    return getItem(position).getClass()
        .getSimpleName();
  }

  @Override public String getItemTag(int position) {
    return String.valueOf(position);
  }

  @Override public StoreContext getItemStore() {
    return storeName == null ? StoreContext.home : StoreContext.meta;
  }
}
