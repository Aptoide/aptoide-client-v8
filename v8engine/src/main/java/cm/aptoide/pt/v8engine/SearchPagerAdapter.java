/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 07/06/2016.
 */

package cm.aptoide.pt.v8engine;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import cm.aptoide.pt.v8engine.fragment.implementations.SearchPagerTabFragment;

/**
 * Created by neuro on 28-04-2016.
 */
public class SearchPagerAdapter extends FragmentStatePagerAdapter {

	private final String query;
	private final boolean hasSubscribedResults;
	private final boolean hasEverywhereResults;

	public SearchPagerAdapter(FragmentManager fm, String query, boolean hasSubscribedResults, boolean
			hasEverywhereResults) {
		super(fm);
		this.query = query;
		this.hasSubscribedResults = hasSubscribedResults;
		this.hasEverywhereResults = hasEverywhereResults;
	}

	@Override
	public Fragment getItem(int position) {
		if (getCount() > 1) {
			if (position == 0) {
				return SearchPagerTabFragment.newInstance(query, true);
			} else if (position == 1) {
				return SearchPagerTabFragment.newInstance(query, false);
			} else {
				throw new IllegalArgumentException("SearchPagerAdapter should have 2 and only 2 pages!");
			}
		} else {
			if (hasSubscribedResults) {
				return SearchPagerTabFragment.newInstance(query, true);
			} else {
				return SearchPagerTabFragment.newInstance(query, false);
			}
		}
	}

	@Override
	public int getCount() {
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
